package com.ll.gramgram.boundedContext.instaMember.entity;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@ToString(callSuper = true)
@Entity
@Getter
@SuperBuilder
public class InstaMember extends InstaMemberBase {
    @Column(unique = true)
    private String username;
    @OneToMany(mappedBy = "fromInstaMember", cascade = CascadeType.ALL)
    @OrderBy("id desc")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default
    private List<LikeablePerson> fromLikeablePeople = new ArrayList<>();
    @OneToMany(mappedBy = "toInstaMember", cascade = CascadeType.ALL)
    @OrderBy("id desc")
    @LazyCollection(LazyCollectionOption.EXTRA)
    @Builder.Default
    private List<LikeablePerson> toLikeablePeople = new ArrayList<>();

    public void addFromLikeablePerson(LikeablePerson likeablePerson) {
        fromLikeablePeople.add(0, likeablePerson);
    }

    public void addToLikeablePerson(LikeablePerson likeablePerson) {
        toLikeablePeople.add(0, likeablePerson);
    }

    public void removeFromLikeablePerson(LikeablePerson likeablePerson) {
        fromLikeablePeople.removeIf(e -> e.equals(likeablePerson));
    }

    public void removeToLikeablePerson(LikeablePerson likeablePerson) {
        toLikeablePeople.removeIf(e -> e.equals(likeablePerson));
    }

    public String getGenderDisplayName() {
        return switch (gender) {
            case "W" -> "여성";
            default -> "남성";
        };
    }

    public void updateGender(String gender) {
        this.gender = gender;
    }
}
