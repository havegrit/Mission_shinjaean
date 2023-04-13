package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class LikeablePersonServiceTests {
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Test
    @DisplayName("modifyAttractionTypeCode() Test")
    void t001() {
        LikeablePerson likeablePerson = likeablePersonService.findById(1L).get();
        likeablePersonService.modifyAttractionTypeCode(likeablePerson, 2);
        assertThat(likeablePersonService.findById(1L).get().getAttractiveTypeCode()).isEqualTo(2);
    }
}
