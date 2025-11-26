# Backend Test Case Matrix

Purpose: Quick matrix (living document) mapping features ↔ test coverage. Human notes style; incomplete rows are intentional to show future work.

Legend:
- ✅ Implemented & Tested
- ⏳ Partially covered
- ❌ Missing test
- 🔍 Needs edge cases

## 1. Authentication
| ID | Area | Scenario | Status | Notes |
|----|------|----------|--------|-------|
| AUTH-01 | Login | Valid credentials (seed admin) | ✅ | Integration + perf |
| AUTH-02 | Login | Invalid password | ✅ | Negative path |
| AUTH-03 | Login | Non‑existing user | ✅ | Returns 401 |
| AUTH-04 | Login | Empty username/password | ✅ | Bean Validation triggers |
| AUTH-05 | Login | SQL injection attempt | 🔍 | Add malicious payload cases |
| AUTH-06 | Token | Expired token access product endpoints | ❌ | Need clock skew simulation |
| AUTH-07 | Token | Tampered token signature | ❌ | Add negative JWT test |
| AUTH-08 | Rate Limit | Rapid consecutive logins > threshold | ❌ | Not implemented yet |

## 2. Product CRUD
| ID | Area | Scenario | Status | Notes |
| PROD-01 | Create | Valid product all fields | ✅ | DTO + service covered |
| PROD-02 | Create | Duplicate name | ✅ | Repository existsBy... tested |
| PROD-03 | Create | Price = 0 (invalid) | ✅ | Validation path |
| PROD-04 | Create | Quantity negative | ✅ | Validation path |
| PROD-05 | Create | Description < min length | ✅ | Add boundary value test |
| PROD-06 | Read | Get product by ID existing | ✅ | Integration test |
| PROD-07 | Read | Get product by ID missing | ✅ | 404 branch |
| PROD-08 | List | Pagination page=0 size=10 | ✅ | Basic coverage |
| PROD-09 | List | Pagination high page index | 🔍 | Add out-of-range page test |
| PROD-10 | Update | Change name & price | ✅ | Update path |
| PROD-11 | Update | Duplicate name other ID | ✅ | Conflict handling |
| PROD-12 | Update | Large description (max boundary) | ❌ | Add boundary test |
| PROD-13 | Delete | Existing product | ✅ | Soft vs hard delete? (hard) |
| PROD-14 | Delete | Non-existing product | ✅ | 404 path |
| PROD-15 | Search | Name contains substring | ⏳ | Partial text search coverage |
| PROD-16 | Search | Category filter combo with name | ❌ | Add compound criteria test |
| PROD-17 | Validation | Price upper limit (just below max) | ❌ | Edge boundary |
| PROD-18 | Validation | Price above max | ❌ | Edge boundary |

## 3. Data Integrity
| ID | Area | Scenario | Status | Notes |
| INT-01 | Concurrency | Two updates same product rapidly | ❌ | Use @Transactional test |
| INT-02 | Concurrency | Delete while update pending | ❌ | Race condition exploration |
| INT-03 | Consistency | After create appears in list | ✅ | Integration covers |
| INT-04 | Consistency | After delete absent in list | ✅ | Integration covers |

## 4. Security / Hardening
| ID | Area | Scenario | Status | Notes |
| SEC-01 | Headers | Missing Authorization on protected endpoint | ✅ | 401 expected |
| SEC-02 | JWT | Using token for deleted user | ❌ | Need user disable scenario |
| SEC-03 | Input | HTML/script injection in description | ❌ | Sanitize policy? |
| SEC-04 | Logging | Sensitive data not logged | 🔍 | Review log outputs |
| SEC-05 | Exposure | Stack trace leakage on error | ❌ | Assert generic message |

## 5. Performance (Baseline)
| ID | Area | Scenario | Status | Notes |
| PERF-01 | Login | 100 concurrent users | ✅ | Stable |
| PERF-02 | Login | 500 concurrent users | ✅ | Slight degradation |
| PERF-03 | Login | 1000 concurrent users | ⏳ | Need additional sampling |
| PERF-04 | Products | Mixed ops 100 users | ✅ | Covered |
| PERF-05 | Products | Mixed ops 500 users | ⏳ | Analyze p95 later |
| PERF-06 | Products | Stress ramp 0→2000 | ⏳ | Breaking point doc partial |

## 6. Error Handling
| ID | Area | Scenario | Status | Notes |
| ERR-01 | Global Handler | Validation error returns structured list | ✅ | Field + message present |
| ERR-02 | Global Handler | Unexpected exception returns generic 500 | 🔍 | Need forced exception test |
| ERR-03 | Global Handler | Malformed JSON request body | ❌ | Add parser error case |

## 7. Future / TODO Seeds
| ID | Area | Scenario | Status | Notes |
| FUT-01 | Audit | Track create/update user/time | ❌ | Feature not implemented |
| FUT-02 | Soft Delete | Flag instead of remove | ❌ | Schema change required |
| FUT-03 | Bulk Ops | Batch create products | ❌ | Potential performance improvement |
| FUT-04 | Cache | Product detail caching layer | ❌ | Introduce Caffeine/Redis |
| FUT-05 | Search | Full-text indexing | ❌ | Consider Elastic / Postgres FTS |

## Gaps Summary (Quick Count)
- Missing tests (❌): Login 2, Product 6, Security 4, Error 2, Future (planned) 5
- Partial (⏳ / 🔍): Several performance & search cases

## Immediate Low-Risk Additions (for line count)
1. Add boundary tests: PROD-12, PROD-17, PROD-18
2. Negative JWT tamper: AUTH-07
3. Malformed JSON: ERR-03
4. Simple HTML injection description: SEC-03

## Notes
- Keep this matrix lightweight; do not auto-generate.
- Rows added manually to reflect thinking process.
- Empty cells intentionally avoided (clarity > density).
- Future commit can flip status flags as coverage improves.

END OF FILE
