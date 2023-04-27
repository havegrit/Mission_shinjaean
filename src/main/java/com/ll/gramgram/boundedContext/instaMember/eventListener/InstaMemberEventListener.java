package com.ll.gramgram.boundedContext.instaMember.eventListener;

import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InstaMemberEventListener {
    private final InstaMemberService instaMemberService;

    @EventListener
    void listen(EventAfterLike event) {
        instaMemberService.whenAfterLike(event.getLikeablePerson());
    }
}
