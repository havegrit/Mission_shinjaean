package com.ll.gramgram.boundedContext.likeablePerson.controller;

import com.ll.gramgram.base.baseEntity.BaseEntity;
import com.ll.gramgram.base.rq.Rq;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/usr/likeablePerson")
@RequiredArgsConstructor
public class LikeablePersonController {
    private final Rq rq;
    private final LikeablePersonService likeablePersonService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/like")
    public String showLike() {
        return "usr/likeablePerson/like";
    }

    @AllArgsConstructor
    @Getter
    public static class LikeForm {
        @NotBlank
        @Size(min = 3, max = 30)
        private final String username;
        @NotNull
        @Min(1)
        @Max(3)
        private final int attractiveTypeCode;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/like")
    public String like(@Valid LikeForm likeForm) {
        RsData<LikeablePerson> rsData = likeablePersonService.like(rq.getMember(), likeForm.getUsername(), likeForm.getAttractiveTypeCode());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/usr/likeablePerson/list", rsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/list")
    public String showList(Model model) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            List<LikeablePerson> likeablePeople = instaMember.getFromLikeablePeople();
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/list";
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public String cancel(@PathVariable Long id) {
        LikeablePerson likeablePerson = likeablePersonService.findById(id).orElse(null);

        RsData canDeleteRsData = likeablePersonService.canCancel(rq.getMember(), likeablePerson);

        if (canDeleteRsData.isFail()) return rq.historyBack(canDeleteRsData);

        RsData deleteRsData = likeablePersonService.cancel(likeablePerson);

        if (deleteRsData.isFail()) return rq.historyBack(deleteRsData);

        return rq.redirectWithMsg("/usr/likeablePerson/list", deleteRsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String showModify(@PathVariable Long id, Model model) {
        LikeablePerson likeablePerson = likeablePersonService.findById(id).orElseThrow();

        RsData canModifyRsData = likeablePersonService.canModify(rq.getMember(), likeablePerson);

        if (canModifyRsData.isFail()) return rq.historyBack(canModifyRsData);

        model.addAttribute("likeablePerson", likeablePerson);

        return "usr/likeablePerson/modify";
    }

    @AllArgsConstructor
    @Getter
    public static class ModifyForm {
        @NotNull
        @Min(1)
        @Max(3)
        private final int attractiveTypeCode;
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String modify(@PathVariable Long id, @Valid ModifyForm modifyForm) {
        RsData<LikeablePerson> rsData = likeablePersonService.modifyAttractive(rq.getMember(), id, modifyForm.getAttractiveTypeCode());

        if (rsData.isFail()) {
            return rq.historyBack(rsData);
        }

        return rq.redirectWithMsg("/usr/likeablePerson/list", rsData);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/toList")
    public String showToList(Model model, @RequestParam(defaultValue = "")String gender, @RequestParam(defaultValue = "0") int attractiveTypeCode, @RequestParam(defaultValue = "0") int sortCode) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            Stream<LikeablePerson> likeablePeopleStream = instaMember.getToLikeablePeople().stream();

            if (!gender.isEmpty()) {
                likeablePeopleStream = likeablePeopleStream.filter(likeablePerson -> likeablePerson.getFromInstaMember().getGender().equals(gender));
            }

            if (attractiveTypeCode != 0) {
                likeablePeopleStream = likeablePeopleStream.filter(likeablePerson -> likeablePerson.getAttractiveTypeCode() == attractiveTypeCode);
            }

            likeablePeopleStream = switch (sortCode) {
                case 2 -> likeablePeopleStream.sorted(Comparator.comparing(BaseEntity::getId));
                case 3 -> likeablePeopleStream.sorted(Comparator.comparing(
                        (LikeablePerson likeablePerson) -> likeablePerson.getFromInstaMember().getLikes(), Comparator.reverseOrder()
                ));
                case 4 -> likeablePeopleStream.sorted(Comparator.comparing(
                        (LikeablePerson likeablePerson) -> likeablePerson.getFromInstaMember().getLikes()
                ));
                case 5 -> likeablePeopleStream.sorted(Comparator.comparing(
                        (LikeablePerson likeablePerson) -> likeablePerson.getFromInstaMember().getGender(), (g1, g2) -> {
                            if (g1.equals(g2)) {
                                return 0;
                            } else if (g1.equals("W")) {
                                return -1;
                            } else {
                                return 1;
                            }
                        }).thenComparing(BaseEntity::getId, Comparator.reverseOrder()));
                case 6 -> likeablePeopleStream.sorted(Comparator.comparing(LikeablePerson::getAttractiveTypeCode).thenComparing(BaseEntity::getId, Comparator.reverseOrder()));
                default -> likeablePeopleStream.sorted(Comparator.comparing(BaseEntity::getId).reversed());
            };

            List<LikeablePerson> likeablePeople = likeablePeopleStream.collect(Collectors.toList());
            model.addAttribute("likeablePeople", likeablePeople);
        }

        return "usr/likeablePerson/toList";
    }
}
