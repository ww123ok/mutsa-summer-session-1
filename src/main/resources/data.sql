-- 1. 회원 세팅 (5000원 보유)
INSERT INTO member (id, email, password, name, credit)
VALUES (1, 'hyeonseong@likelion.org', 'password123', '현성', 5000);

-- 2. 가게 세팅
INSERT INTO store (id, name, category, rating, image_url)
VALUES (1, '냠냠 분식', 'KOREAN', 4.4, 'http://image.url');

-- 3. 메뉴 및 옵션 세팅 (떡볶이 3000원, 치즈추가 1000원)
INSERT INTO menu (id, store_id, name, price, description, is_multiple)
VALUES (1, 1, '떡볶이', 3000, '매콤달콤 떡볶이', FALSE);

INSERT INTO menu_option (id, menu_id, name, additional_price)
VALUES (101, 1, '치즈 추가', 1000);

-- 4. 장바구니 및 장바구니 아이템 세팅 (현성이의 장바구니에 떡볶이 1개 담기)
INSERT INTO cart (id, member_id)
VALUES (1, 1);

INSERT INTO cart_item (id, cart_id, menu_id, quantity)
VALUES (1, 1, 1, 1);

-- 5. 장바구니 아이템에 치즈 추가 옵션 달아주기
INSERT INTO cart_item_option (id, cart_item_id, menu_option_id)
VALUES (1, 1, 1);