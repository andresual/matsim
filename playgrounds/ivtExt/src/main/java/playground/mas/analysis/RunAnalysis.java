package playground.mas.analysis;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.Scenario;
import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.population.Person;
import org.matsim.api.core.v01.population.Population;
import org.matsim.core.api.experimental.events.EventsManager;
import org.matsim.core.config.Config;
import org.matsim.core.config.ConfigUtils;
import org.matsim.core.events.EventsUtils;
import org.matsim.core.events.MatsimEventsReader;
import org.matsim.core.population.PopulationUtils;
import org.matsim.core.population.io.PopulationReader;
import org.matsim.core.scenario.ScenarioUtils;
import playground.mas.MASConfigGroup;
import playground.mas.MASModule;
import playground.mas.cordon.CordonState;
import playground.mas.cordon.IntervalCordonState;
import playground.mas.cordon.MASCordonUtils;
import playground.mas.zurich.ZurichMASConfigGroup;
import playground.sebhoerl.av_paper.BinCalculator;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.stream.Collectors;

public class RunAnalysis {
    static public void main(String[] args) throws IOException {
        String configPath = args[0];
        String eventsPath = args[1];
        String populationPath = args[2];
        String outputPath = args[3];

        ZurichMASConfigGroup zurichMASConfigGroup = new ZurichMASConfigGroup();

        MASConfigGroup masConfigGroup = new MASConfigGroup();
        Config config = ConfigUtils.loadConfig(configPath, masConfigGroup);

        config.plans().setInputFile(populationPath);

        IntervalCordonState cordonState = new IntervalCordonState();
        new IntervalCordonState.Reader(cordonState).read(masConfigGroup.getCordonIntervals());

        Scenario scenario = ScenarioUtils.loadScenario(config);

        Collection<Link> cordonLinks = MASCordonUtils.findChargeableCordonLinks(masConfigGroup.getCordonCenterNodeId(), masConfigGroup.getCordonRadius(), scenario.getNetwork());
        Collection<Link> insideLinks = MASCordonUtils.findInsideCordonLinks(masConfigGroup.getCordonCenterNodeId(), masConfigGroup.getCordonRadius(), scenario.getNetwork());

        Collection<Id<Link>> allLinks = scenario.getNetwork().getLinks().values().stream().map(l -> l.getId()).collect(Collectors.toList());

        if (zurichMASConfigGroup.getAVAreaCenterNodeId() != null) {
            allLinks = MASCordonUtils.findInsideCordonLinks(zurichMASConfigGroup.getAVAreaCenterNodeId(), zurichMASConfigGroup.getAVAreaRadius(), scenario.getNetwork())
                    .stream().map(l -> l.getId()).collect(Collectors.toList());
        }

        Collection<Id<Link>> cordonLinkIds = cordonLinks.stream().map(l -> l.getId()).collect(Collectors.toList());
        Collection<Id<Link>> insideLinkIds = insideLinks.stream().map(l -> l.getId()).collect(Collectors.toList());
        Collection<Id<Link>> outsideLinkIds = allLinks.stream().filter(id -> !insideLinkIds.contains(id)).collect(Collectors.toList());

        Collection<Id<Person>> evPersonIds = new MASModule().provideEVUserIds(scenario.getPopulation());

        BinCalculator binCalculator = BinCalculator.createByInterval(0.0, 24.0 * 3600.0, 300.0);
        DataFrame dataFrame = new DataFrame(binCalculator);

        EventsManager eventsManager = EventsUtils.createEventsManager(config);
        eventsManager.addHandler(new CountsHandler(dataFrame, binCalculator, insideLinkIds, outsideLinkIds, evPersonIds));
        eventsManager.addHandler(new CordonHandler(dataFrame, binCalculator, evPersonIds, masConfigGroup.getChargedOperatorIds(), cordonLinkIds, cordonState));
        eventsManager.addHandler(new DistancesHandler(dataFrame, binCalculator, scenario.getNetwork(), evPersonIds));
        eventsManager.addHandler(new AVHandler(dataFrame, binCalculator));

        new MatsimEventsReader(eventsManager).readFile(eventsPath);
        new ScoresReader(dataFrame).read(scenario.getPopulation());
        new SlowModeReader(dataFrame, binCalculator).read(scenario.getPopulation());

        (new ObjectMapper()).writeValue(new File(outputPath), dataFrame);
    }
}
