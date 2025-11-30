package com.rushcrew.common.global;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
public class AuditorAwareImpl implements AuditorAware<Long> {
    // Gateway에서 넣어주는 헤더 이름
    private static final String USER_ID_HEADER = "X-User-Id";

    @Override
    public Optional<Long> getCurrentAuditor() {
        return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
            .map(attributes -> (ServletRequestAttributes) attributes)
            .map(ServletRequestAttributes::getRequest)
            .map(request -> request.getHeader(USER_ID_HEADER))
            .filter(StringUtils::hasText)
            .map(userIdStr -> {
                try {
                    return Long.parseLong(userIdStr);
                } catch (NumberFormatException e) {
                    log.warn("[Common] Invalid User ID format in header: {}", userIdStr);
                    // 파싱 실패 시 null (Auditing 생략)
                    return null;
                }
            });
    }
}
