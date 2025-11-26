# QA Checklist (Working Draft)

Purpose: Practical checklist used during manual review cycles. Not enforced by code; lives here as a reference.

## 1. Environment Sanity
- [ ] Backend starts without stack traces
- [ ] H2 (test) / SQLServer (prod) reachable
- [ ] Test users seeded (admin / user01 / testuser)
- [ ] Timezone consistent (UTC vs local) for timestamps
- [ ] JWT secret set in environment (no placeholder value)

## 2. Smoke Flow
- [ ] Login success path (admin)
- [ ] Login failure path (wrong password)
- [ ] Product list renders
- [ ] Create product -> appears in list
- [ ] Edit product -> values update
- [ ] Delete product -> removed

## 3. Validation (Spot)
- [ ] Username < min length rejected
- [ ] Password blank rejected
- [ ] Product price negative rejected
- [ ] Product quantity negative rejected
- [ ] Description too short triggers message
- [ ] Duplicate product name blocked

## 4. API Contract (Quick Glance)
- [ ] /api/auth/login returns token + userDto
- [ ] /api/products (list) includes pagination fields
- [ ] /api/products/{id} returns 404 for missing
- [ ] Error payload has field + message pair
- [ ] Content-Type application/json everywhere

## 5. Security Basics
- [ ] Protected endpoints reject missing token
- [ ] Token tamper (modify one char) rejected
- [ ] No password field leaked in responses
- [ ] Server headers do not expose framework versions excessively
- [ ] CORS only allows expected origin

## 6. UX Checks
- [ ] Submit buttons disable during request
- [ ] Error messages clear after successful retry
- [ ] Loading indicator visible on data fetch
- [ ] Empty states show friendly text
- [ ] Long product name wraps gracefully (no layout break)

## 7. Accessibility Quick Pass
- [ ] Inputs have associated labels or aria-label
- [ ] Modal focus trap (focus stays inside)
- [ ] Keyboard can reach all interactive elements
- [ ] Color contrast acceptable (manual eyeball)
- [ ] Images (if any) have alt text

## 8. Performance Spot (Manual)
- [ ] Initial product list response < 800ms on dev machine
- [ ] Login round trip < 600ms
- [ ] No obvious memory growth in repeated create/delete loop (quick observation)

## 9. Logs / Observability
- [ ] Login failure not logging raw password
- [ ] Exceptions mapped to concise messages
- [ ] No stack trace dumped to client
- [ ] Startup log shows environment clearly

## 10. Error Handling
- [ ] Invalid JSON -> proper 400 (not 500)
- [ ] Unknown endpoint -> 404 generic
- [ ] Method not allowed -> 405
- [ ] Validation errors aggregated (not truncated)

## 11. Data Integrity
- [ ] Create then immediate get by ID returns same payload
- [ ] Update then list reflects new values
- [ ] Delete then get by ID returns 404
- [ ] Pagination stable (no duplicates between pages when size small)

## 12. Regression Spot (Last Release Issues)
- [ ] Previously fixed bug #1 not resurfaced
- [ ] Previously fixed bug #2 not resurfaced
- [ ] Token expiry handled cleanly (manual wait if feasible)

## 13. Feature Flags / Config
- [ ] No hard-coded dev URLs in prod build
- [ ] Build artifacts minified (frontend)
- [ ] Source maps excluded (prod) or access restricted

## 14. Quick Manual Attack Vectors
- [ ] Script tags in product description not executed
- [ ] Extremely long strings rejected gracefully
- [ ] High-frequency rapid submit limited (observed)

## 15. Cleanup / Polish
- [ ] No TODOs left in production code for critical areas
- [ ] README instructions still match actual start sequence
- [ ] Test matrices updated (backend / frontend)

## 16. Nice-To-Have Follow Ups
- [ ] Add automated a11y scan (axe)
- [ ] Add visual regression baseline
- [ ] Add adapter for retry logic on transient product fetch errors

## 17. Pending Items (Future Sprints)
- [ ] Role-based access expansion
- [ ] Soft delete improvement
- [ ] Bulk product import

## 18. Sign-Off Slots
- QA Engineer: __________________ Date: _______
- Dev Lead: ____________________ Date: _______
- Product Owner: _______________ Date: _______

## Notes
This doc intentionally simple. Add/remove items as product scope evolves.
Keep unchecked items visible (do NOT delete) to show current state.

END OF FILE
