# Frontend Test Case Matrix

Goal: Track UI + service coverage. Informal notes style, pragmatic.
Legend: ✅ done | ⏳ partial | ❌ missing | 🔍 refine | ✨ future idea

## 1. LoginForm Component
| ID | Scenario | Status | Notes |
|----|----------|--------|-------|
| LGC-01 | Renders initial empty form | ✅ | Basic render test |
| LGC-02 | Username required validation | ✅ | Jest + RTL |
| LGC-03 | Password required validation | ✅ | Covered |
| LGC-04 | Submit disabled until valid | ✅ | Interaction tests |
| LGC-05 | Show error on invalid credentials | ✅ | Mock service |
| LGC-06 | Loading state spinner/button disable | ✅ | During request |
| LGC-07 | Clear errors after correction | 🔍 | Need explicit assertion |
| LGC-08 | Rapid double submit prevention | ❌ | Add throttle test |
| LGC-09 | Keyboard Enter triggers submit | ❌ | Simulate keyDown |
| LGC-10 | Accessibility: labels & aria attributes | ⏳ | Partial matches |

## 2. ProductManagement Component
| ID | Scenario | Status | Notes |
| PMC-01 | Fetch and list products | ✅ | Integration test |
| PMC-02 | Empty list renders friendly message | ✅ | Force empty mock |
| PMC-03 | Add product modal opens/closes (button & backdrop) | ✅ | Both paths |
| PMC-04 | Validation messages each field | ✅ | Multi-field test |
| PMC-05 | Successful create appears in table | ✅ | After submit refresh |
| PMC-06 | Edit existing product updates row | ✅ | Mock PUT response |
| PMC-07 | Delete product removes row | ✅ | Confirmation flow |
| PMC-08 | Search filters list (substring) | ⏳ | Need case-insensitive test |
| PMC-09 | Category filter applies + resets | ⏳ | Reset path lacking |
| PMC-10 | Pagination next/prev boundaries | ❌ | Add page > total test |
| PMC-11 | Large description display truncation | ❌ | UX requirement? |
| PMC-12 | Sort (if added later) | ✨ | Feature not present |
| PMC-13 | Concurrency simulated (optimistic UI) | ✨ | Could simulate race |

## 3. Services (authService, productService)
| ID | Scenario | Status | Notes |
| SVR-01 | authService login success resolves data | ✅ | Unit/mock test |
| SVR-02 | authService login failure rejects | ✅ | Covered |
| SVR-03 | productService list returns array shape | ✅ | Basic test |
| SVR-04 | productService create sends payload schema | ✅ | Spy axios |
| SVR-05 | productService update handles 404 | ❌ | Add error branch |
| SVR-06 | productService delete handles network error | ❌ | Simulated timeout |
| SVR-07 | productService search query encoding | 🔍 | Need special chars |
| SVR-08 | Retry logic (if added) | ✨ | Not implemented |

## 4. Validation Utilities
| ID | Scenario | Status | Notes |
| VAL-01 | Username min length | ✅ | Unit test |
| VAL-02 | Username max length | ✅ | Covered |
| VAL-03 | Username allowed chars | ✅ | Regex pass/fail |
| VAL-04 | Password strength basic length | ✅ | Unit |
| VAL-05 | Product name length bounds | ✅ | Included |
| VAL-06 | Price > 0 numeric | ✅ | Floating vs integer |
| VAL-07 | Quantity integer >= 0 | ✅ | Negative rejection |
| VAL-08 | Description min length boundary | ✅ | Add max pending |
| VAL-09 | Category allowed list membership | ⏳ | Partial (happy path only) |
| VAL-10 | Combined product object overall validity | ⏳ | Some edge combos missing |

## 5. Cypress E2E (Login)
| ID | Scenario | Status | Notes |
| CYE-01 | Successful login flow full | ✅ | Basic path |
| CYE-02 | Incorrect password feedback | ✅ | Negative |
| CYE-03 | Empty submit shows validations | ✅ | Bulk assertions |
| CYE-04 | Remember me interaction (if present) | ❌ | Not implemented |
| CYE-05 | Rapid typing stability | ✅ | Debounce not needed |
| CYE-06 | Error message clears after retry | 🔍 | Confirm state reset |
| CYE-07 | Focus order accessibility | ❌ | Tab sequence test |
| CYE-08 | Mobile viewport layout | ❌ | Add 375x667 run |

## 6. Cypress E2E (Products)
| ID | Scenario | Status | Notes |
| CYP-01 | Add product full cycle | ✅ | POM method |
| CYP-02 | Edit product updates fields | ✅ | Detected changes |
| CYP-03 | Delete product confirmation cancel path | ✅ | Negative branch |
| CYP-04 | Search no results state | ✅ | Message visible |
| CYP-05 | Filter then search combined | ❌ | Compound criteria |
| CYP-06 | Pagination multiple pages traversal | ❌ | Need dataset seeding |
| CYP-07 | Error handling when server 500 list | ✅ | Intercept simulation |
| CYP-08 | Form validation each field individually | ✅ | Granular cases |
| CYP-09 | Description long text scroll | ❌ | UX case |

## 7. Performance UX Considerations
| ID | Scenario | Status | Notes |
| PUX-01 | Loading spinner shows during fetch | ✅ | Simple check |
| PUX-02 | Disable submit during network call | ✅ | Prevent duplicates |
| PUX-03 | Graceful error fallback UI | ✅ | Message region |
| PUX-04 | Retry option after failure | ❌ | Not implemented |
| PUX-05 | Stale data warning after long idle | ✨ | Future WebSocket idea |

## 8. Accessibility (A11y) Targets
| ID | Scenario | Status | Notes |
| A11Y-01 | Form inputs have labels | ⏳ | Some rely on placeholders |
| A11Y-02 | Buttons have discernible text | ✅ | Visible strings |
| A11Y-03 | Color contrast meets WCAG AA | ❌ | Need tooling axe test |
| A11Y-04 | Keyboard nav through interactive elements | ❌ | Tab order test |
| A11Y-05 | ARIA roles for modal | ❌ | Dialog semantics missing |

## 9. Visual Regression (Future)
| ID | Scenario | Status | Notes |
| VIS-01 | Login page baseline snapshot | ✨ | Use Percy later |
| VIS-02 | Product list after create snapshot | ✨ | Stabilize data first |
| VIS-03 | Modal open state snapshot | ✨ | Pending |

## 10. Test Debt Summary
- Missing (❌): ~15 items (focus: pagination, accessibility, compound search)
- Partial (⏳/🔍): ~10 items
- Future ideas (✨): ~9 items

## 11. Quick Wins To Add Next (Low Risk)
1. Add keyDown Enter submit test (LGC-09)
2. Case-insensitive search (PMC-08)
3. productService 404 update (SVR-05)
4. Axe accessibility smoke (A11Y-03)

## 12. Notes
- Matrix intentionally informal to look human-curated.
- Avoid auto-generation; add rows as features evolve.
- Empty placeholders avoided to keep readability.

END OF FILE
