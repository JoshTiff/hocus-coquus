package com.hocuscoquus.hocuscoquus.model;

import jakarta.validation.constraints.NotNull;

public record ReactionCreateRequest(
		@NotNull ReactionType reactionType
) {}
