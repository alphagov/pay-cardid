package uk.gov.pay.card.bench;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import uk.gov.pay.card.db.CardInformationStore;
import uk.gov.pay.card.db.InfinispanCardInformationStore;
import uk.gov.pay.card.db.loader.WorldpayBinRangeLoader;
import uk.gov.pay.card.model.CardInformation;

import java.net.URL;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/***
 * To run this Benchmark against the whole of worldpay BIN range data set;
 * - Make a copy of the worldpay data into the test/rsoureces/ folder.
 * - Change the name of the file to load in this benchmark (in the setup()) method.
 * - You would have to replace the existing test card prefixes with some new prefixes.
 * - And then run the benchmark.
 */
@State(Scope.Benchmark)
public class InfinispanCardInformationStoreBenchmark {

    private CardInformationStore cardInformationStore;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        URL url = this.getClass().getResource("/worldpay-bin-ranges.csv");
        WorldpayBinRangeLoader worldpayBinRangeLoader = new WorldpayBinRangeLoader(url.getFile());
        cardInformationStore = new InfinispanCardInformationStore(worldpayBinRangeLoader);
        cardInformationStore.initialiseCardInformation();
    }

    @Param({
            "511226112", "511226113", "511226114", "511226115", "511226116", "511226117", "511226118", "511226119", "511226120",
            "511226122", "511226123", "511226124", "511226125", "511226126", "511226127", "511226128", "511226129", "511226130",
            "511948111", "511948112", "511948113", "511948114", "511948115", "511948116", "511948117", "511948118", "511948119"
    })
    public String cardIdPrefix;

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
                .include(InfinispanCardInformationStoreBenchmark.class.getSimpleName())
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
