package com.itea.messenger.dto.user.responses;

import com.itea.messenger.dto.user.ModificationStatus;
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
    private ModificationStatus modificationStatus;
}