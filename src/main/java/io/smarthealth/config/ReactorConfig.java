//package io.smarthealth.config;
//
//import io.smarthealth.sequence.SequenceEvent;
//import io.smarthealth.sequence.SequenceNumberService; 
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j; 
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.web.context.request.async.DeferredResult;
//import reactor.core.Environment;
//import reactor.core.Reactor;
//import reactor.core.spec.Reactors;
//import reactor.event.Event;
//import static reactor.event.selector.Selectors.$;
//import reactor.function.Consumer;
//
///**
// *
// * @author Kelsas
// */
//@Configuration
//@Slf4j
//@RequiredArgsConstructor
//public class ReactorConfig { 
//    private final SequenceNumberService sequenceNumberService;
//
//    @Bean
//    public Reactor reactor(final Environment env) {
//        Reactor reactor = Reactors.reactor(env, Environment.RING_BUFFER);
//
//        reactor.on($("sequence"), new Consumer<Event<SequenceEvent>>() {
//            public void accept(Event<SequenceEvent> ev) {
//                SequenceEvent data = ev.getData();
//                DeferredResult<String> result = data.getResult();
//                try {
//                    String number = sequenceNumberService.next(data.getTenant(), data.getName());
//                    result.setResult("{ \"number\": \"" + number + "\" }\n");
//                } catch (Exception e) {
//                    log.error("Exception in sequence event listener", e);
//                    result.setErrorResult(e);
//                }
//            }
//        });
//
//        return reactor;
//    }
//}
