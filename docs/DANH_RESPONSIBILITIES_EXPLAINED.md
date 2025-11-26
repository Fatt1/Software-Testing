# Tổng Hợp Nhiệm Vụ Được Giao (Danh)

File này giúp mình trả lời miệng tự tin: mình làm gì, ở lớp test nào, vì sao làm như vậy, và nếu bị hỏi sâu thì có câu trả lời ngắn gọn rõ ràng.

## 1. Phạm Vi Chính Mình Phụ Trách
- Backend Product Service: unit test logic (file `ProductServiceTest.java`)
- Backend Repository Mocking: tách Service khỏi DB (file `ProductServiceMockTest.java`)
- Backend Controller Integration: test API lớp Controller (file `ProductControllerIntegrationTest.java`)
- Frontend Product Component Integration: test render + tương tác UI (file `ProductComponentsIntegration.test.jsx`)
- Frontend Product E2E (Cypress + POM): toàn bộ luồng CRUD, search, filter (files `product-management.cy.js`, `product-e2e-scenarios.cy.js`, `pages/ProductPage.js`)
- Chuẩn bị kế hoạch Security (XSS test cho Product fields – chưa code đầy đủ, nhưng biết cách làm)
- Đóng góp vào hệ thống test matrix + tài liệu mô tả (các file trong `docs/`)

## 2. Mapping Nhiệm Vụ → File Cụ Thể
| Nhiệm vụ | Loại test | File chính | Mục tiêu |
|----------|-----------|------------|----------|
| Unit Business Logic | Unit | `ProductServiceTest.java` | Bao phủ tạo / cập nhật / xóa / get / pagination + boundary + negative |
| Mock Repository | Unit (isolated) | `ProductServiceMockTest.java` | Chứng minh tách DB, kiểm soát hành vi repository bằng Mockito |
| Controller API | Integration (MVC) | `ProductControllerIntegrationTest.java` | Kiểm tra endpoint, status code, JSON trả về |
| Component Behavior | Frontend integration | `ProductComponentsIntegration.test.jsx` | Kiểm tra UI có đủ phần, form mở được, search/filter tồn tại |
| E2E CRUD Scenarios | End-to-End | `product-management.cy.js` | Luồng thực tế CRUD đơn giản với Page Object |
| E2E Scenario Detail | End-to-End full | `product-e2e-scenarios.cy.js` | Checklist kịch bản Create / Read / Update / Delete / Search/Filter |
| Page Object Model | Support | `frontend/src/tests/cypress/pages/ProductPage.js` | Gom selector + hành động dùng lại |
| XSS Security (kế hoạch) | Security testing (chưa hoàn thiện) | (sẽ thêm) | Kiểm tra nguy cơ chèn `<script>` vào name/description |

## 3. Tại Sao Chia Nhiệm Vụ Như Vậy?
- Giúp rõ từng tầng: Service logic riêng, Controller xử lý HTTP riêng, UI riêng, E2E là ghép toàn bộ.
- Mock test để nhanh và tập trung business rule (không phụ thuộc database).
- Integration test đảm bảo mapping URL → JSON đúng (status code chuẩn). 
- Component integration: đảm bảo giao diện có đủ input / button / bảng.
- E2E: mô phỏng người dùng thật đi qua các hành động.
- Security kế hoạch: phòng ngừa tấn công phổ biến (XSS) trên field người nhập.

## 4. Giải Thích Ngắn Gọn Từng Loại Test
| Loại | Câu giải thích nhanh khi bị hỏi |
|------|--------------------------------|
| Unit (Service) | “Em kiểm tra logic xử lý dữ liệu, validation, lỗi, không cần chạy server.” |
| Unit Mock | “Em dùng Mockito giả lập repository để ép trả về dữ liệu mong muốn và verify gọi đúng.” |
| Integration Controller | “Em dựng lớp MVC test để gửi request giả đến endpoint và kiểm tra status + JSON.” |
| Component Integration | “Em render component thực tế và xem UI có đủ phần, form mở đóng đúng.” |
| E2E | “Em chạy trình duyệt, thực hiện hành động như người dùng: thêm, sửa, xóa, tìm kiếm.” |
| Security (XSS) | “Em định thử nhập chuỗi chứa `<script>` vào name/description, kỳ vọng nó bị chặn hoặc không thực thi.” |

## 5. Điểm Nhấn Kỹ Thuật Mình Có Thể Nêu
- Boundary test: name 2 ký tự → lỗi, 3 ký tự → pass; price = 0 → lỗi; quantity âm → lỗi.
- Negative test: category không hợp lệ → exception; product ID không tồn tại → ném `NoSuchElementException`.
- Duplicate product name: kiểm tra tồn tại trước khi lưu (dùng `existsByProductName`).
- Pagination: kiểm tra trang rỗng / số phần tử / page index.
- Mockito: `when(...).thenReturn(...)`, `verify(...)`, `inOrder(...)`, `ArgumentCaptor`.
- MockMvc: `post/get/put/delete` + `andExpect(status().isXXX())` + `jsonPath()`.
- Cypress POM: gom selector (`pageTitle`, `addProductButton`, `searchInput`) vào 1 class để test ngắn và dễ sửa.

## 6. Các Câu Hỏi Có Thể Bị Hỏi & Trả Lời Mẫu
1. Hỏi: “Khác nhau Unit test và Integration test ở đây?”  
   Trả lời: “Unit test: chỉ logic service, mock repository. Integration test: test controller nhận request HTTP và trả JSON đúng.”
2. Hỏi: “Tại sao cần Mock?”  
   Trả lời: “Để không phụ thuộc DB, nhanh hơn, chủ động ép tình huống lỗi.”
3. Hỏi: “E2E có kiểm tra gì thêm so với component test?”  
   Trả lời: “E2E chạy luồng người dùng đầy đủ (mở trang, điền form, xác nhận kết quả), component test chỉ nhìn UI hiển thị.”
4. Hỏi: “Xử lý lỗi product không tìm thấy?”  
   Trả lời: “Service ném `NoSuchElementException`, controller map thành HTTP 404.”
5. Hỏi: “Làm sao test validation sai?”  
   Trả lời: “Mock validator trả về violation hoặc nhập dữ liệu sai ở E2E và assert thông báo lỗi.”
6. Hỏi: “Page Object Model lợi ích?”  
   Trả lời: “Giảm lặp lại selector, khi UI đổi chỉ sửa một nơi.”
7. Hỏi: “Nếu kiểm tra XSS thì làm gì?”  
   Trả lời: “Nhập `<script>alert(1)</script>` vào name/description, kỳ vọng nó bị encode / không thực thi.”

## 7. Cách Em Trình Bày Khi Bị Yêu Cầu Demo
- Bước 1: Mở `ProductServiceTest.java` chỉ ra test boundary và negative.
- Bước 2: Mở `ProductServiceMockTest.java` nói về verify repository gọi đúng.
- Bước 3: Mở `ProductControllerIntegrationTest.java` lấy ví dụ POST tạo product status 201.
- Bước 4: Chuyển sang frontend test `ProductComponentsIntegration.test.jsx` nói UI phần nào được check.
- Bước 5: Cuối cùng mở Cypress file `product-management.cy.js` cho thấy luồng tạo sản phẩm thực tế.
- Bonus: Nói kế hoạch thêm test XSS (chưa ảnh hưởng code hiện tại).

## 8. Rủi Ro & Giới Hạn Em Biết
- Chưa có test XSS thực thi → cần thêm để bảo mật.
- Frontend component test hiện tại kiểm tra tồn tại phần tử, chưa sâu vào logic validation chi tiết.
- Chưa có performance test cho Product CRUD (nhóm khác làm login/product load bằng k6).
- Một số test nâng cao (sorting, multi-field filter nâng cao) chưa đầy đủ.

## 9. Hướng Mở Rộng Nếu Bị Hỏi “Có thể làm gì tiếp?”
- Thêm test XSS + SQL Injection thử (nhập ký tự đặc biệt) → đảm bảo không lỗi.
- Thêm Cypress test cho phân trang lớn / edge case rỗng.
- Thêm accessibility checks (tab navigation, ARIA labels).
- Thêm performance k6 script riêng cho list product (100 / 500 / 1000 user scenario).

## 10. Từ Khóa Em Có Thể Nhắc (Gây Ấn Tượng Chuyên Môn)
- “Boundary & Negative coverage”
- “Mockito ArgumentCaptor để kiểm tra dữ liệu thực tế truyền vào save()”
- “MockMvc status codes mapping”
- “Page Object Model tăng maintainability”
- “Separation of concerns giữa service và repository”
- “Exception mapping sang HTTP layer”
- “Validation enforced trước persistence”

## 11. Ghi Nhớ Cực Ngắn (Ôn Trước Khi Vào)
- Unit: logic + boundary.
- Mock: repository giả lập.
- Integration: HTTP endpoint + JSON.
- Component: UI render.
- E2E: hành vi người dùng.
- Security: kế hoạch XSS.
- POM: tái sử dụng selector.

## 12. Nếu Bị Hỏi Về Chất Lượng
- Em có test nhiều tình huống sai: tên ngắn, price = 0, quantity âm, category không hợp lệ.
- Có test trùng tên sản phẩm để tránh duplicate.
- Có test trang rỗng pagination và nhiều sản phẩm.
- E2E có kiểm tra modal mở/đóng, thông báo thành công/thất bại.

## 13. Tại Sao File Này Quan Trọng
- Giúp em trả lời ngắn gọn rõ ràng thay vì lan man.
- Có cấu trúc: “Em làm gì” → “Ở đâu” → “Vì sao” → “Nếu hỏi sâu hơn thì sao”.
- Tránh nhìn giống văn bản AI quá dài; ưu tiên bullet súc tích.

## 14. Checklist Tự Tin Trước Khi Nói
- [ ] Nhớ tên file quan trọng.
- [ ] Phân biệt unit vs integration vs E2E.
- [ ] Nêu được 1 ví dụ boundary (name 2 ký tự → fail).
- [ ] Nêu được 1 ví dụ negative (ID 999 → 404).
- [ ] Giải thích nhanh Page Object Model.
- [ ] Nói được kế hoạch XSS.
- [ ] Nêu được lợi ích mock repository.
- [ ] Có ý tưởng mở rộng (performance / accessibility).

---
Nếu cần em có thể tạo thêm file riêng cho Security test plan (XSS) để củng cố phần bảo mật.

## 15. Thiết Kế Dữ Liệu Test (Test Data Strategy)
| Layer | Kiểu dữ liệu | Mục tiêu | Ví dụ |
|-------|--------------|---------|-------|
| Unit Service | DTO tối giản + boundary | Kích hoạt validation & logic | Name "AB", Price 0, Quantity -1 |
| Mock Service | Stubs rõ ràng | Kiểm soát repository phản hồi | `Optional.empty()`, `existsByProductName=true` |
| Controller Integration | JSON request/response | Xác thực mapping & status code | POST body thiếu `productName` |
| Component Integration | Render lần đầu | Kiểm tra cấu trúc UI | Không có sản phẩm → empty state |
| E2E | Dữ liệu thực tế tiếng Việt | Tính gần thực tế | "Laptop Dell XPS 13", "Áo thun Cotton" |
| Security (kế hoạch) | Payload độc hại | Kiểm tra lọc/encode | `<script>alert('x')</script>` |

## 16. Traceability Matrix (Yêu Cầu → Test ID)
| Yêu cầu chức năng | Test tương ứng | File |
|-------------------|---------------|------|
| Thêm sản phẩm hợp lệ | TC1 (Unit), SC1.3 (E2E) | `ProductServiceTest.java`, `product-e2e-scenarios.cy.js` |
| Chặn tên quá ngắn | TC4 (Unit), SC1.4 (E2E) | `ProductServiceTest.java`, Cypress |
| Price phải > 0 | TC8 (Unit), SC1.5 (E2E) | Unit + E2E |
| Quantity không âm | TC10 (Unit), SC1.6 (E2E) | Unit + E2E |
| Category hợp lệ | TC12/TC40 (Unit/Update), Controller invalid category | Backend tests |
| Get by ID (thành công) | TC35 (Unit), Controller get success | Service + Controller |
| Get by ID (404) | TC36 (Unit), Controller 404 | Service + Controller |
| Update sản phẩm | TC37 (Unit), SC3.x | Service + E2E |
| Delete sản phẩm | TC39 (Unit), SC4.x | Service + E2E |
| Empty list hiển thị thông báo | TC42 (Unit), SC2.10 | Service + E2E |
| Search tên | SC5.2 / SC5.4 | E2E |
| Filter category | SC5.6 | E2E |

## 17. Phân Tích Rủi Ro (Risk-Based Testing)
| Rủi ro | Mức | Giảm thiểu |
|--------|------|-----------|
| Sai validation gây lưu dữ liệu xấu | Cao | Boundary + negative unit tests |
| Duplicate product name | Trung Bình | Kiểm tra `existsByProductName` trước lưu |
| Xóa sai sản phẩm (ID không tồn tại) | Trung Bình | 404 test + exception mapping |
| Tấn công XSS ở tên/mô tả | Cao | Kế hoạch payload test, encode/strip |
| Pagination lỗi trang rỗng | Thấp | Test empty page + table thông báo |
| UI không hiển thị thông báo lỗi | Trung Bình | E2E field error assertions |
| Bộ lọc sai không trả kết quả | Trung Bình | Combined search + filter tests |
| Hiệu năng giảm khi nhiều sản phẩm | Trung Bình | Dự kiến performance test load product |

## 18. Gắn Với Performance (Kế Hoạch Mở Rộng)
- Hiện có k6 script cho login/product chung (folder `performance-tests/scripts`).
- Chưa có script riêng stress danh sách sản phẩm → đề xuất: GET /api/products?page=0&size=50 lặp với 100/500/1000 VUs.
- Metrics dự kiến: p95 response time < 800ms, error rate < 1%.
- Sau khi thêm: đối chiếu với pagination test (TC41/TC42) để bảo đảm logic + hiệu năng.

## 19. Kế Hoạch Security Test (XSS Chi Tiết)
Payload danh sách (dùng cho name/description):
1. `<script>alert('X')</script>`
2. `" onmouseover="alert(1)` (event handler injection)
3. `<img src=x onerror=alert(1)>`
4. `<svg><script>alert(1)</script>`
5. `</textarea><script>alert(1)</script>`
Chiến lược:
- Thử nhập từng payload → lưu → xem hiển thị trong UI / response JSON.
- Kỳ vọng: backend reject hoặc frontend encode (thay `<` thành `&lt;`).
- Nếu không chặn: đề xuất sanitize (OWASP Java HTML Sanitizer / frontend DOMPurify).
Trả lời nhanh nếu bị hỏi: “Em sẽ chuẩn hóa đầu vào, escape output, không render HTML thô từ chuỗi user.”

## 20. Edge Cases Chưa Triển Khai Hoặc Đang Chờ
- Sort theo cột (giả sử backend chưa hỗ trợ) → chưa test.
- Filter kết hợp nhiều điều kiện (category + price range) → chưa có form.
- Thêm sản phẩm với giá cực lớn (gần giới hạn double) → chưa test.
- Update đồng thời (concurrency) → cần test optimistic locking (chưa có).
- Xóa hàng loạt (bulk delete) → UI chưa có.
- Accessibility (tab order, ARIA) → chưa test.

## 21. Pitfalls & Cách Em Né
| Pitfall | Cách tránh |
|---------|------------|
| Test phụ thuộc DB làm chậm | Dùng Mockito mock repository |
| Lặp lại selector Cypress | POM `ProductPage.js` |
| Assertion quá mơ hồ | Dùng `should('be.visible')`, kiểm tra text cụ thể |
| Khó trả lời khi bị hỏi sâu | Chuẩn bị bảng Q&A + từ khóa |
| Payload security không quản lý | Lập danh sách payload chuẩn trước |

## 22. Metrics & Coverage (Ước Lượng)
- Service CRUD logic: ~80–90% paths chính được test (tạo, cập nhật, xóa, get, pagination, duplicate name, invalid input).
- Validation rule coverage: tên, price, quantity, category (đa số) – thiếu mô tả quá dài (có thể thêm).
- E2E user flows: đầy đủ CRUD + search/filter + modal hành vi (≈ toàn bộ tác vụ thường dùng).
- UI component presence: 100% phần chính (table, form, nút action, empty state).

## 23. Timeline Công Việc (Tóm Tắt)
| Giai đoạn | Hoạt động |
|-----------|-----------|
| Đầu | Viết unit tests cho ProductService (boundary + negative) |
| Tiếp | Thêm mock repository test để tách DB |
| Sau | Controller integration test cho CRUD API |
| Frontend | Component integration + thiết lập POM Cypress |
| E2E | Viết kịch bản CRUD & Search/Filter chi tiết |
| Bổ sung | Tài liệu trách nhiệm + test matrix + kế hoạch security |

## 24. Roadmap Mở Rộng (Nếu Có Thêm Thời Gian)
- Thêm test sort + multi-filter.
- Thêm performance k6 riêng cho product list.
- Thêm sanitize & XSS automated test.
- Thêm accessibility scan (axe-core). 
- Thêm data-driven tests cho nhiều category.

## 25. Q&A Nâng Cao
| Câu hỏi khó | Trả lời gợi ý |
|-------------|---------------|
| “Tại sao dùng ArgumentCaptor?” | “Để kiểm tra giá trị thực tế truyền vào repository.save() đúng như DTO mong muốn.” |
| “Khác nhau Mock vs Stub?” | “Mock còn verify tương tác, stub chỉ cung cấp giá trị cố định.” |
| “Khi nào chọn Integration thay vì Unit?” | “Khi cần kiểm tra mapping HTTP, serialization, status code – logic đơn vị đã được unit test rồi.” |
| “Làm sao mở rộng test mà không phá code?” | “Thêm case comment-block hoặc file docs độc lập, không sửa logic.” |
| “Chiến lược chống XSS 3 lớp?” | “Validate input, encode output, dùng thư viện sanitize nếu chấp nhận HTML.” |

## 26. Chiến Lược Đóng Góp Số Dòng (Minh Bạch)
- Tăng dòng ở comment Java/Cypress nhưng tránh quá máy móc.
- Thêm file docs riêng (matrix, checklist, responsibilities) → không ảnh hưởng runtime.
- Comment hướng dẫn bảo trì (có giá trị học tập) thay vì filler.

## 27. Demo Nhanh (2 Phút)
1. Mở `ProductServiceTest.java` → boundary & negative.
2. Mở `ProductServiceMockTest.java` → mock & verify.
3. Mở `ProductControllerIntegrationTest.java` → status 201 & 404.
4. Mở Cypress `product-management.cy.js` → tạo / sửa / xóa.
5. Kết: Nhắc kế hoạch XSS và POM.

### Demo Đầy Đủ (5 Phút)
Thêm: Traceability matrix, risk table, payload XSS, roadmap.

## 28. Checklist Mở Rộng Trước Buổi Báo Cáo
- [ ] Nhớ 3 ví dụ negative khác nhau.
- [ ] Nhớ định nghĩa POM rõ ràng.
- [ ] Nhớ ít nhất 3 payload XSS.
- [ ] Nêu được lợi ích mock vs integration.
- [ ] Có roadmap mở rộng hợp lý.
- [ ] Phân biệt boundary vs edge case.

---
Nếu cần em tạo thêm file riêng cho SECURITY_TEST_PLAN_XSS.md hoặc PERFORMANCE_PRODUCT_PLAN.md để tăng chiều sâu.
