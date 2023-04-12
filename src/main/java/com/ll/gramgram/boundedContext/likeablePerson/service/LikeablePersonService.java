package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (!member.hasConnectedInstaMember()) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();

        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember) // 호감을 표시하는 사람의 인스타 멤버
                .fromInstaMemberUsername(member.getInstaMember().getUsername()) // 중요하지 않음
                .toInstaMember(toInstaMember) // 호감을 받는 사람의 인스타 멤버
                .toInstaMemberUsername(toInstaMember.getUsername()) // 중요하지 않음
                .attractiveTypeCode(attractiveTypeCode) // 1=외모, 2=능력, 3=성격
                .build();

        likeablePersonRepository.save(likeablePerson); // 저장

        fromInstaMember.addFromLikeablePerson(likeablePerson);
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록하였습니다.".formatted(username), likeablePerson);
    }

    public List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    @Transactional
    public RsData<LikeablePerson> delete(InstaMember instaMember, Long id) {
        Optional<LikeablePerson> likeablePerson = likeablePersonRepository.findById(id);
        Long actorInstaMemberId = instaMember.getId();
        if (likeablePerson.isEmpty()) {
            return RsData.of("F-3", "잘못된 접근입니다.");
        }
        Long fromInstaMemberId = likeablePerson.get().getFromInstaMember().getId();
        if (!fromInstaMemberId.equals(actorInstaMemberId)) {
            return RsData.of("F-4", "데이터를 삭제할 권한이 없습니다.");
        }
        String cancelTargetUsername = likeablePerson.get().getFromInstaMember().getUsername();
        likeablePersonRepository.delete(likeablePerson.get());
        return RsData.of("S-1", "%s님에 대한 데이터를 성공적으로 삭제했습니다.".formatted(cancelTargetUsername));
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    @Transactional
    public RsData<LikeablePerson> modifyAttractionTypeCode(LikeablePerson likeablePerson, int attractionTypeCode){
        String beforeAttractionType = attractionTypeCodeToString(likeablePerson.getAttractiveTypeCode());
        String afterAttractionType = attractionTypeCodeToString(attractionTypeCode);
        likeablePerson.setAttractiveTypeCode(attractionTypeCode);
        likeablePerson.setModifyDate(LocalDateTime.now());
        likeablePersonRepository.save(likeablePerson);
        return RsData.of("S-2", "%s에 대한 호감 사유를 %s에서 %s(으)로 변경합니다.".formatted(likeablePerson.getToInstaMember().getUsername(), beforeAttractionType, afterAttractionType));
    }

    public String attractionTypeCodeToString(int attractionTypeCode) {
        String attractionType;
        switch (attractionTypeCode) {
            case 1 -> attractionType = "외모";
            case 2 -> attractionType = "성격";
            default -> attractionType = "능력";
        }
        return attractionType;
    }
}
