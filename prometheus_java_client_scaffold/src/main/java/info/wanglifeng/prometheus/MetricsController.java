package info.wanglifeng.prometheus;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Random;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;
import io.prometheus.client.Summary;
import io.prometheus.client.exporter.MetricsServlet;

/**
 * Created by wesley on 8/27/16.
 */

@Controller
@RequestMapping("/metrics")
public class MetricsController extends MetricsServlet {
    static String[] labelNames = {"label1", "label2"};
    static final Counter counter = Counter.build()
            .name("counter_name")
            .help("Number of hello worlds served.").register();

    static final Gauge gauge = Gauge.build()
            .name("gauge_name")
            .help("number of gauge").register();

    static final Histogram histogram = Histogram.build()
            .buckets(.001, .01, .1, 1.0, 10, 100, 1000)
            .name("histogram_name")
            .help("number of histogram").register();

    static final Summary receivedBytes = Summary.build()
            .name("requests_size_bytes")
            .help("Request size in bytes.")
            .quantile(0.1, 0.001)
            .quantile(0.2, 0.002)
            .quantile(0.3, 0.003)
            .quantile(0.4, 0.004)
            .quantile(0.5, 0.005)
            .quantile(0.6, 0.006)
            .quantile(0.7, 0.007)
            .quantile(0.8, 0.008)
            .quantile(0.9, 0.009)
            .quantile(0.99, 0.001)
            .register();
    static final Summary requestLatency = Summary.build()
            .name("requests_latency_seconds")
            .help("Request latency in seconds.")
            .quantile(0.01, 0.0001)
            .quantile(0.5, 0.05)
            .quantile(0.9, 0.01)
            .quantile(0.99, 0.001)
            .register();

    @RequestMapping()
    public void metrics(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException, InterruptedException {
        // counter
        counter.inc();
        // gauge
        Random rand = new Random();
        gauge.set(rand.nextDouble() * 100);

        // histogram
        Histogram.Timer histogramRequestTimer = histogram.startTimer();
        histogramRequestTimer.observeDuration();

        // summary
        Summary.Timer summaryReuestTimer = requestLatency.startTimer();
        receivedBytes.observe(rand.nextDouble() * 1500);
        summaryReuestTimer.observeDuration();
        super.doGet(req, res);
    }
}
