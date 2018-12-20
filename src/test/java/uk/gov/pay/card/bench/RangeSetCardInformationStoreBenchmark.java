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

    @Setup(Level.Trial)
    public void setup() throws Exception {
        URL url = this.getClass().getResource("/worldpay/");
        BinRangeDataLoader worldpayBinRangeLoader = BinRangeDataLoaderFactory.worldpay(url.getFile());
        cardInformationStore = new RangeSetCardInformationStore(singletonList(worldpayBinRangeLoader));
        cardInformationStore.initialiseCardInformation();
    }

    @Param({
            "51122666112", "51122666113", "51122666114", "51122666115", "51122666116", "51122666117", "51122666118", "51122666119", "51122666120",
            "51122666122", "51122666123", "51122666124", "51122666125", "51122666126", "51122666127", "51122666128", "51122666129", "51122666130",
            "511948111", "511948112", "511948113", "511948114", "511948115", "511948116", "511948117", "511948118", "511948119"
    })
    private String cardIdPrefix;

    @Benchmark
    public void runBenchmark(Blackhole blackhole) {
        Optional<CardInformation> cardInformation = cardInformationStore.find(cardIdPrefix);
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
