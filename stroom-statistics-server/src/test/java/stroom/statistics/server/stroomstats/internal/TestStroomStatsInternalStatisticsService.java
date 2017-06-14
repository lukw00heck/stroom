package stroom.statistics.server.stroomstats.internal;

import com.google.common.collect.ImmutableMap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import stroom.kafka.StroomKafkaProducer;
import stroom.node.server.MockStroomPropertyService;
import stroom.query.api.v1.DocRef;
import stroom.statistics.internal.InternalStatisticEvent;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

@RunWith(MockitoJUnitRunner.class)
public class TestStroomStatsInternalStatisticsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TestStroomStatsInternalStatisticsService.class);

    public static final String DOC_REF_TYPE = "myDocRefType";

    private final MockStroomPropertyService mockStroomPropertyService = new MockStroomPropertyService();

    @Mock
    private StroomKafkaProducer stroomKafkaProducer;

    @Captor
    ArgumentCaptor<Consumer<Exception>> exceptionHandlerCaptor;

    @Test(expected = RuntimeException.class)
    public void putEvents_exception() {

        mockStroomPropertyService.setProperty(StroomStatsInternalStatisticsService.PROP_KEY_DOC_REF_TYPE, DOC_REF_TYPE);
        mockStroomPropertyService.setProperty(
                StroomStatsInternalStatisticsService.PROP_KEY_PREFIX_KAFKA_TOPICS +
                        InternalStatisticEvent.Type.COUNT.toString().toLowerCase(),
                "MyTopic");

//        Mockito.doNothing()
//                .when(stroomKafkaProducer)
//                .send(Mockito.any(), Mockito.any(), Mockito.any());


        //when flush is called on the producer capture the exceptionhandler passed to the send method and give an exception
        //to the handler to simulate a failure on the send that will only manifest itself on the flush
        Mockito.doAnswer(invocation -> {
            Mockito.verify(stroomKafkaProducer)
                    .send(Mockito.any(), Mockito.any(), exceptionHandlerCaptor.capture());
            exceptionHandlerCaptor.getValue()
                    .accept(new RuntimeException("Exception inside StroomKafkaProducer"));
            return null;
        }).when(stroomKafkaProducer).flush();

        StroomStatsInternalStatisticsService stroomStatsInternalStatisticsService = new StroomStatsInternalStatisticsService(
                stroomKafkaProducer,
                mockStroomPropertyService
        );

        //assemble test data
        InternalStatisticEvent event = InternalStatisticEvent.createPlusOneCountStat("myKey", 0, Collections.emptyMap());
        DocRef docRef = new DocRef(DOC_REF_TYPE, UUID.randomUUID().toString(), "myStat");
        Map<DocRef, List<InternalStatisticEvent>> map = ImmutableMap.of(docRef, Collections.singletonList(event));

        //exercise the service
        try {
            stroomStatsInternalStatisticsService.putEvents(map);
        } catch (Exception e) {
            LOGGER.info("Caught expected exception: {} ", e.getMessage(), e);
            throw e;
        }
    }

}