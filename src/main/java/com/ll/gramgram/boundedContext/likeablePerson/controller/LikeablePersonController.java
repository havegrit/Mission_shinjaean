package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @GetMapping("/add")
    public String showAdd() {
        return "usr/likeablePerson/add";
    }

    @AllArgsConstructor
    @Getter
    public static class AddForm {
        private final String username;
        private final int attractiveTypeCode;
    }

    @PostMapping("/add")
    public String add(@Valid AddForm addForm) {
        Member loginUser = rq.getMember();
        InstaMember userInstaMember = loginUser.getInstaMember();
        List<LikeablePerson> likeablePersonList = userInstaMember.getFromLikeablePeople();
        String registeringUsername = addForm.getUsername().trim();
        Optional<LikeablePerson> likeablePerson = likeablePersonService.findByFromInstaMemberIdAndToInstaMember_username(userInstaMember.getId(), registeringUsername);
        if (likeablePerson.isPresent()) {
            if (likeablePerson.get().getAttractiveTypeCode() != addForm.getAttractiveTypeCode()) {
                RsData editRsData = likeablePersonService.modifyAttractionTypeCode(likeablePerson.get(), addForm.getAttractiveTypeCode());
                return rq.redirectWithMsg("/likeablePerson/list", editRsData);
            }
            return rq.historyBack(RsData.of("F-1", "(%s)님은 이미 호감 상대로 등록한 회원입니다.".formatted(registeringUsername)));
        }
        if (likeablePersonList.size() >= AppConfig.getLikeablePersonFromMax()) {
            return rq.historyBack(RsData.of("F-2", "호감 상대는 %d명 까지 등록할 수 있습니다.".formatted(AppConfig.getLikeablePersonFromMax())));
        }
        RsData createRsData = likeablePersonService.like(rq.getMember(), registeringUsername, addForm.getAttractiveTypeCode());

        if (createRsData.isFail()) {
            return rq.historyBack(createRsData);
        }

        return rq.redirectWithMsg("/likeablePerson/list", createRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id) {
        Member loginMember = rq.getMember();
        if (loginMember.getInstaMember() == null) {
            return rq.historyBack(RsData.of("F-1","잘못된 접근입니다."));
        }
        RsData deleteRsData = likeablePersonService.delete(loginMember.getInstaMember(), id);
        if (deleteRsData.isFail()) {
            return rq.historyBack(deleteRsData);
        }
        return rq.redirectWithMsg("/likeablePerson/list", deleteRsData);
    }

    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            List<LikeablePerson> likeablePeople = instaMember.getFromLikeablePeople();
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }
}
