import http from "k6/http";
import { check, sleep } from "k6";
import { Rate, Trend, Counter } from "k6/metrics";
import { BASE_URL, TEST_USERS } from "./config.js";

// Custom metrics
const getProductsSuccessRate = new Rate("get_products_success_rate");
const getProductByIdSuccessRate = new Rate("get_product_by_id_success_rate");
const createProductSuccessRate = new Rate("create_product_success_rate");
const updateProductSuccessRate = new Rate("update_product_success_rate");
const deleteProductSuccessRate = new Rate("delete_product_success_rate");

const getProductsDuration = new Trend("get_products_duration", true);
const getProductByIdDuration = new Trend("get_product_by_id_duration", true);
const createProductDuration = new Trend("create_product_duration", true);
const updateProductDuration = new Trend("update_product_duration", true);
const deleteProductDuration = new Trend("delete_product_duration", true);

const totalRequests = new Counter("total_requests");
const successfulRequests = new Counter("successful_requests");
const failedRequests = new Counter("failed_requests");

// Test scenarios
export const options = {
  // LOAD TEST: 100 concurrent users
  scenarios: {
    load_100: {
      executor: "constant-vus",
      vus: 100,
      duration: "5m",
    },
  },

  // // LOAD TEST: 500 concurrent users
  // scenarios: {
  //     load_500: {
  //         executor: 'constant-vus',
  //         vus: 500,
  //         duration: '5m',
  //     },
  // },

  // // LOAD TEST: 1000 concurrent users
  // scenarios: {
  //     load_1000: {
  //         executor: 'constant-vus',
  //         vus: 1000,
  //         duration: '5m',
  //     },
  // },

  // // STRESS TEST: Tìm breaking point
  // scenarios: {
  //     stress_test: {
  //         executor: 'ramping-vus',
  //         startVUs: 0,
  //         stages: [
  //             { duration: '2m', target: 100 },
  //             { duration: '5m', target: 100 },
  //             { duration: '2m', target: 500 },
  //             { duration: '5m', target: 500 },
  //             { duration: '2m', target: 1000 },
  //             { duration: '5m', target: 1000 },
  //             { duration: '2m', target: 2000 },
  //             { duration: '5m', target: 2000 },
  //             { duration: '5m', target: 5000 }, // Tìm breaking point
  //             { duration: '2m', target: 0 },
  //         ],
  //     },
  // },

  thresholds: {
    http_req_failed: ["rate<0.01"], // < 1% errors
    http_req_duration: ["p(95)<2000", "p(99)<3000"],
    get_products_success_rate: ["rate>0.99"],
    get_product_by_id_success_rate: ["rate>0.95"],
  },
};

// Danh sách product IDs để test (sẽ được populate trong setup)
let productIds = [];

export function setup() {
  console.log("=== Starting Product API Performance Test ===");
  console.log(`Base URL: ${BASE_URL}`);

  // Login để lấy token (nếu cần authentication)
  const loginRes = http.post(
    `${BASE_URL}/api/auth/login`,
    JSON.stringify({
      userName: TEST_USERS[0].userName,
      password: TEST_USERS[0].password,
    }),
    {
      headers: { "Content-Type": "application/json" },
    }
  );

  let token = null;
  if (loginRes.status === 200) {
    try {
      token = JSON.parse(loginRes.body).token;
      console.log("✅ Login successful, got token");
    } catch (e) {
      console.log("⚠️  Could not parse login response");
    }
  }

  // Lấy danh sách products có sẵn
  const productsRes = http.get(`${BASE_URL}/api/products?page=0&size=100`);
  if (productsRes.status === 200) {
    try {
      const products = JSON.parse(productsRes.body).content;
      productIds = products.map((p) => p.id);
      console.log(`✅ Found ${productIds.length} products in database`);
    } catch (e) {
      console.log("⚠️  Could not parse products response");
    }
  }

  return { token, productIds };
}

export default function (data) {
  totalRequests.add(1);

  const params = {
    headers: {
      "Content-Type": "application/json",
    },
  };

  // Nếu có token, thêm vào header
  if (data.token) {
    params.headers["Authorization"] = `Bearer ${data.token}`;
  }

  // Chọn ngẫu nhiên một operation
  const operations = [
    "getAll", // 40% - GET all products
    "getById", // 30% - GET product by ID
    "create", // 15% - CREATE product
    "update", // 10% - UPDATE product
    "delete", // 5% - DELETE product
  ];

  const weights = [40, 30, 15, 10, 5];
  const totalWeight = weights.reduce((a, b) => a + b, 0);
  const random = Math.random() * totalWeight;

  let cumulativeWeight = 0;
  let selectedOperation = operations[0];

  for (let i = 0; i < operations.length; i++) {
    cumulativeWeight += weights[i];
    if (random <= cumulativeWeight) {
      selectedOperation = operations[i];
      break;
    }
  }

  // Execute selected operation
  switch (selectedOperation) {
    case "getAll":
      testGetAllProducts(params);
      break;
    case "getById":
      testGetProductById(data.productIds, params);
      break;
    case "create":
      testCreateProduct(params);
      break;
    case "update":
      testUpdateProduct(data.productIds, params);
      break;
    case "delete":
      testDeleteProduct(data.productIds, params);
      break;
  }

  // Think time
  sleep(Math.random() * 2 + 0.5); // 0.5-2.5 giây
}

function testGetAllProducts(params) {
  const page = Math.floor(Math.random() * 10);
  const size = 10;

  const startTime = new Date();
  const response = http.get(
    `${BASE_URL}/api/products?page=${page}&size=${size}`,
    params
  );
  const duration = new Date() - startTime;

  getProductsDuration.add(duration);

  const success = check(response, {
    "GET /products - status is 200": (r) => r.status === 200,
    "GET /products - has content": (r) => {
      try {
        return JSON.parse(r.body).content !== undefined;
      } catch (e) {
        return false;
      }
    },
    "GET /products - response time < 2s": (r) => r.timings.duration < 2000,
  });

  if (success) {
    successfulRequests.add(1);
    getProductsSuccessRate.add(1);
  } else {
    failedRequests.add(1);
    getProductsSuccessRate.add(0);
  }
}

function testGetProductById(productIds, params) {
  if (!productIds || productIds.length === 0) {
    return;
  }

  const productId = productIds[Math.floor(Math.random() * productIds.length)];

  const startTime = new Date();
  const response = http.get(`${BASE_URL}/api/products/${productId}`, params);
  const duration = new Date() - startTime;

  getProductByIdDuration.add(duration);

  const success = check(response, {
    "GET /products/{id} - status is 200": (r) => r.status === 200,
    "GET /products/{id} - has id": (r) => {
      try {
        return JSON.parse(r.body).id !== undefined;
      } catch (e) {
        return false;
      }
    },
    "GET /products/{id} - response time < 1.5s": (r) =>
      r.timings.duration < 1500,
  });

  if (success) {
    successfulRequests.add(1);
    getProductByIdSuccessRate.add(1);
  } else {
    failedRequests.add(1);
    getProductByIdSuccessRate.add(0);
  }
}

function testCreateProduct(params) {
  const categories = ["Electronics", "Books", "Clothing", "Toys", "Groceries"];
  const randomCategory =
    categories[Math.floor(Math.random() * categories.length)];
  const randomId = Math.floor(Math.random() * 100000);

  const payload = JSON.stringify({
    productName: `Test Product ${randomId}`,
    price: Math.random() * 10000 + 100,
    description: `Performance test product ${randomId}`,
    quantity: Math.floor(Math.random() * 100),
    category: randomCategory,
  });

  const startTime = new Date();
  const response = http.post(`${BASE_URL}/api/products`, payload, params);
  const duration = new Date() - startTime;

  createProductDuration.add(duration);

  const success = check(response, {
    "POST /products - status is 201": (r) => r.status === 201,
    "POST /products - has id": (r) => {
      try {
        return JSON.parse(r.body).id !== undefined;
      } catch (e) {
        return false;
      }
    },
    "POST /products - response time < 3s": (r) => r.timings.duration < 3000,
  });

  if (success) {
    successfulRequests.add(1);
    createProductSuccessRate.add(1);
  } else {
    failedRequests.add(1);
    createProductSuccessRate.add(0);
  }
}

function testUpdateProduct(productIds, params) {
  if (!productIds || productIds.length === 0) {
    return;
  }

  const productId = productIds[Math.floor(Math.random() * productIds.length)];
  const categories = ["Electronics", "Books", "Clothing", "Toys", "Groceries"];
  const randomCategory =
    categories[Math.floor(Math.random() * categories.length)];

  const payload = JSON.stringify({
    productName: `Updated Product ${productId}`,
    price: Math.random() * 10000 + 100,
    description: `Updated description`,
    quantity: Math.floor(Math.random() * 100),
    category: randomCategory,
  });

  const startTime = new Date();
  const response = http.put(
    `${BASE_URL}/api/products/${productId}`,
    payload,
    params
  );
  const duration = new Date() - startTime;

  updateProductDuration.add(duration);

  const success = check(response, {
    "PUT /products/{id} - status is 200": (r) => r.status === 200,
    "PUT /products/{id} - has id": (r) => {
      try {
        return JSON.parse(r.body).id !== undefined;
      } catch (e) {
        return false;
      }
    },
    "PUT /products/{id} - response time < 3s": (r) => r.timings.duration < 3000,
  });

  if (success) {
    successfulRequests.add(1);
    updateProductSuccessRate.add(1);
  } else {
    failedRequests.add(1);
    updateProductSuccessRate.add(0);
  }
}

function testDeleteProduct(productIds, params) {
  if (!productIds || productIds.length === 0) {
    return;
  }

  // Không nên delete products có sẵn trong performance test
  // Thay vào đó, delete các products đã tạo trong test
  // Ở đây chỉ test với random ID để kiểm tra 404 response
  const randomId = Math.floor(Math.random() * 100000) + 10000;

  const startTime = new Date();
  const response = http.del(
    `${BASE_URL}/api/products/${randomId}`,
    null,
    params
  );
  const duration = new Date() - startTime;

  deleteProductDuration.add(duration);

  const success = check(response, {
    "DELETE /products/{id} - status is 204 or 404": (r) =>
      r.status === 204 || r.status === 404,
    "DELETE /products/{id} - response time < 2s": (r) =>
      r.timings.duration < 2000,
  });

  if (success) {
    successfulRequests.add(1);
    deleteProductSuccessRate.add(1);
  } else {
    failedRequests.add(1);
    deleteProductSuccessRate.add(0);
  }
}

export function teardown(data) {
  console.log("=== Product API Performance Test Completed ===");
}
