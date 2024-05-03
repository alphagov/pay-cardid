package uk.gov.pay.card.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.RangeSetCardInformationStore;
import uk.gov.pay.card.db.loader.BinRangeDataLoader;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static java.util.Collections.singletonList;
import static uk.gov.pay.card.db.loader.BinRangeDataLoader.BinRangeDataLoaderFactory;

/***
 * To run this Benchmark against the whole of worldpay BIN range data set;
 * - Make a copy of the worldpay data into the test/resources/ folder.
 * - Change the name of the file to load in this benchmark (in the setup()) method.
 * - You would have to replace the existing test card prefixes with some new prefixes.
 * - And then run the benchmark.
 */
@State(Scope.Benchmark)
public class RangeSetCardInformationStoreBenchmark {

    private CardInformationStore cardInformationStore;

    @Param({
            "511226661120000000", "511226661130000000", "511226661140000000", "511226661150000000", "511226661160000000", "511226661170000000", "511226661180000000", "511226661190000000", "511226661200000000",
            "511226661220000000", "511226661230000000", "511226661240000000", "511226661250000000", "511226661260000000", "511226661270000000", "511226661280000000", "511226661290000000", "511226661300000000",
            "511948111", "511948112", "511948113", "511948114", "511948115", "511948116", "511948117", "511948118", "511948119"
    })
    private String cardIdPrefix;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url);
        cardInformationStore = new RangeSetCardInformationStore(singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();
    }

    @Benchmark
    public void runBenchmark(Blackhole blackhole) {
        Optional<CardInformation> cardInformation = cardInformationStore.find(Long.valueOf(cardIdPrefix));
        cardInformation.orElseThrow(() -> new RuntimeException("card information not found"));
        blackhole.consume(cardInformation);
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .warmupIterations(5)
                .measurementIterations(20)
                .forks(1)
                .threads(5)
                .include(RangeSetCardInformationStoreBenchmark.class.getSimpleName())
                .timeUnit(TimeUnit.MICROSECONDS)
                .mode(Mode.AverageTime)
                .build();

        new Runner(options).run();
    }

    @TearDown(Level.Trial)
    public void tearDown() {
        cardInformationStore.destroy();
    }
}
