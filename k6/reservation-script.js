import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
    scenarios: {
        gradual_uinque_users: {
            executor: 'per-vu-iterations',
            vus: 1000,            // 1000명의 가상 사용자
            iterations: 1,        // 각 사용자 1회 요청
            maxDuration: '5m',    // 최대 실행 시간
            startTime: '0s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'],  // 95% 요청이 500ms 이하
        http_req_failed: ['rate<0.05'],   // 실패율 5% 이하
    },
};

const BASE_URL = 'http://host.docker.internal:8080';

export default function () {
    const userId = 'user' + __VU;

    // 토큰 발급
    let createTokenResponse = http.post(`${BASE_URL}/queues`,
        JSON.stringify({ userId: userId }),
        { headers: { 'Content-Type': 'application/json' } }
    );

    const tokenId = createTokenResponse.json().tokenId;
    console.log('토큰 ID : ' + tokenId);

    // 토큰 상태 확인
    let status = 'WAIT';
    let attempts = 0;
    const maxAttempts = 15;

    while (status !== 'ACTIVE' && attempts < maxAttempts) {
        sleep(3);

        let queueStatusResponse = http.get(`${BASE_URL}/queues/${userId}`,
            { headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + tokenId }}
        );

        status = queueStatusResponse.status === 200 ? queueStatusResponse.json().status : status;

        if (status === 'ACTIVE') break;

        attempts++;
    }

    // 토큰 활성화 좌석 예약
    if (status === 'ACTIVE') {
        const concertId = 1;
        const scheduleId = 1;
        const seatId = 1;

        let reservationResponse = http.post(`${BASE_URL}/reservations`,
            JSON.stringify({
                userId: userId,
                concertId: concertId,
                scheduleId: scheduleId,
                seatId: seatId
            }),
            { headers: { 'Content-Type': 'application/json', 'Authorization': 'Bearer ' + tokenId }}
        );

        check(reservationResponse, {
            'Seat: reservation success': (r) => r.status === 201,
            'Seat: reservation conflict': (r) => r.status === 409
        });
    } else {
        console.log('토큰이 활성화되지 않았습니다.');
    }

    sleep(1);
}