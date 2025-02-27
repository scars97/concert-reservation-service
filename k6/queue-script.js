import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    stages: [
        { duration: '20s', target: 50 }, // 20초 동안 20명 → 50명으로 증가
        { duration: '20s', target: 50 }, // 50명 부하 유지 (peak 구간)
        { duration: '20s', target: 0 },  // 20초 동안 부하 감소
    ],
    scenarios: {
        unique_requests: {
            executor: 'per-vu-iterations',
            vus: 50, // 최대 동시 사용자 수 (peak)
            iterations: 1, // 사용자별 1회 요청
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'],  // 95% 요청이 500ms 이하
        http_req_failed: ['rate<0.05'],   // 실패율 5% 이하
    },
};

const BASE_URL = 'http://host.docker.internal:8080';

let userCount = 1;

export default function () {
    const userId = 'user' + (userCount++); // 사용자 ID 범위: 1 ~ 1000
    if (userCount > 1000) return;

    // 토큰 발급
    let tokenResponse = http.post(`${BASE_URL}/queues`,
        JSON.stringify({ userId: userId }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    check(tokenResponse, { '토큰 생성' : (r) => r.status === 201 });

    sleep(1);
}