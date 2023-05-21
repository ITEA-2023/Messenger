package com.itea.messenger.dto.responses;

import com.itea.messenger.dto.ModificationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserModificationResponse {
    private String username;
    private ModificationStatus modificationType;
}