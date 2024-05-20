package com.ssafy.double_bean.story_play.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.UUID;

public record StoryPlayingCreateRequestDto(
        @JsonDeserialize
        UUID spotUuid,
        String jsonEventContent
) {
}
