> https://github.com/emilybache/GildedRose-Refactoring-Kata

## 목적

GildedRose-Refactoring-Kata에서 영감을 받아, 최근 업무에서 만난 케이스에 대한 팀원들의 접근방식이 궁금하여 준비하게 됨.

## 요구사항 명세

- 해외 파트너사들의 정산내역 집계 데이터를 기초로 하여, 필요한 세부 금액 항목들을 계산하여 반환한다.
- `Input`은 Input 형식으로 특정 정산월 해외 파트너사의 발생한 총 원화 인접권료, 그리고 적용 환율, 통화, 원천세율 등의 정보가 있다.
- `Output`은 Output 형식으로 `Input`를 기초로 하여 각 항목을 계산한 결과를 담는다.

### 산식

- 원화는 모두 정수로 반올림
- 외화는 엔(JPY)일 경우 정수로 반올림, 나머지(USD, EUR)는 소숫점 둘째 자리까지 반올림
- `Input`의 값을 그대로 전달하는 숫자 항목 (ex, exchangeRate, neighboringCopyrightFee, withholdingTaxRate)은 따로 반올림, 자릿수 등 변환하지 않는다.
- 산식을 구성하는 각 요소들은 모두 그 자체로 반올림 된 상태에서 차용되어야함
  - ex) `외화 지급총액 = 외화 소득세 + 외화 주민세 + 외화 순지급액` 이라는 산식이 있을 때, 최종 외화 지급총액을 구한 뒤 반올림 처리하는 것이 아닌 외화 소득세, 외화 주민세, 외화 순지급액 각각
    기준에 따라 반올림 처리가 된 후 합산되어야 한다.

#### 1. PAYEE 유형

1. 원화 지급총액 krwTotalPayment

- `외화 지급총액 * 환율`

2. 원화 소득세 krwIncomeTax

- `외화 소득세 * 환율`

3. 원화 주민세 krwResidentTax

- `외화 주민세 * 환율`

4. 원화 순지급액 krwNetPayment

- `원화 지급총액 - (원화 소득세 + 원화 주민세)`

5. 외화 지급총액 foreignTotalPayment

- `원화 인접권료 / 환율`

6. 외화 소득세 foreignIncomeTax

- `외화 지급총액 * 원천세율 / 1.1`

7. 외화 주민세 foreignResidentTax

- `외화 소득세 * 0.1`

8. 외화 순지급액 foreignNetPayment

- `외화 지급총액 - (외화 소득세 + 외화 주민세)`

#### 2. PAYER 유형

1. 원화 지급총액 krwTotalPayment

- `원화 소득세 + 원화 주민세 + 원화 순지급액`

2. 원화 소득세 krwIncomeTax

- `외화 소득세 * 환율`

3. 원화 주민세 krwResidentTax

- `외화 주민세 * 환율`

4. 원화 순지급액 krwNetPayment

- `외화 순지급액 * 환율`

5. 외화 지급총액 foreignTotalPayment

- `외화 소득세 + 외화 주민세 + 외화 순지급액`

6. 외화 소득세 foreignIncomeTax

- `외화 순지급액 / (1- 원천세율) - 외화 순지급액) / 1.1`

7. 외화 주민세 foreignResidentTax

- `외화 소득세 * 0.1`

8. 외화 순지급액 foreignNetPayment

- `원화 인접권료 / 환율`
