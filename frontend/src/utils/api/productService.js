// Product categories
export const categories = [
  'Điện tử',
  'Thời trang',
  'Đồ gia dụng',
  'Sách',
  'Thực phẩm'
];

// Mock product database using localStorage
const getProducts = () => {
  const products = {};
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i);
    if (key.startsWith('product:')) {
      products[key] = JSON.parse(localStorage.getItem(key));
    }
  }
  return products;
};

const generateId = () => {
  return Date.now().toString();
};

// Mock API endpoints
export const getAllProducts = async () => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 300));
  return Object.values(getProducts());
};

export const getProduct = async (id) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 200));
  const product = localStorage.getItem(`product:${id}`);
  if (!product) {
    throw new Error('Không tìm thấy sản phẩm');
  }
  return JSON.parse(product);
};

export const createProduct = async (productData) => {
  // Validate input
  if (!productData.name?.trim()) {
    throw new Error('Tên sản phẩm không được để trống');
  }
  if (productData.name.length > 100) {
    throw new Error('Tên sản phẩm không được vượt quá 100 ký tự');
  }
  if (!productData.price) {
    throw new Error('Giá không được để trống');
  }
  if (isNaN(productData.price) || productData.price < 0) {
    throw new Error('Giá không hợp lệ');
  }
  if (!productData.quantity) {
    throw new Error('Số lượng không được để trống');
  }
  if (isNaN(productData.quantity) || productData.quantity < 0) {
    throw new Error('Số lượng không hợp lệ');
  }
  if (!productData.category) {
    throw new Error('Danh mục không được để trống');
  }
  if (!categories.includes(productData.category)) {
    throw new Error('Danh mục không hợp lệ');
  }
  if (!productData.description?.trim()) {
    throw new Error('Mô tả không được để trống');
  }
  if (productData.description.length > 500) {
    throw new Error('Mô tả không được vượt quá 500 ký tự');
  }

  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 500));

  const id = generateId();
  const product = {
    id,
    ...productData,
    createdAt: new Date().toISOString()
  };

  localStorage.setItem(`product:${id}`, JSON.stringify(product));
  return product;
};

export const updateProduct = async (id, productData) => {
  // Validate product exists
  const existingProduct = await getProduct(id);
  if (!existingProduct) {
    throw new Error('Không tìm thấy sản phẩm');
  }

  // Validate input (same as create)
  if (!productData.name?.trim()) {
    throw new Error('Tên sản phẩm không được để trống');
  }
  if (productData.name.length > 100) {
    throw new Error('Tên sản phẩm không được vượt quá 100 ký tự');
  }
  if (!productData.price) {
    throw new Error('Giá không được để trống');
  }
  if (isNaN(productData.price) || productData.price < 0) {
    throw new Error('Giá không hợp lệ');
  }
  if (!productData.quantity) {
    throw new Error('Số lượng không được để trống');
  }
  if (isNaN(productData.quantity) || productData.quantity < 0) {
    throw new Error('Số lượng không hợp lệ');
  }
  if (!productData.category) {
    throw new Error('Danh mục không được để trống');
  }
  if (!categories.includes(productData.category)) {
    throw new Error('Danh mục không hợp lệ');
  }
  if (!productData.description?.trim()) {
    throw new Error('Mô tả không được để trống');
  }
  if (productData.description.length > 500) {
    throw new Error('Mô tả không được vượt quá 500 ký tự');
  }

  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 500));

  const updatedProduct = {
    ...existingProduct,
    ...productData,
    updatedAt: new Date().toISOString()
  };

  localStorage.setItem(`product:${id}`, JSON.stringify(updatedProduct));
  return updatedProduct;
};

export const deleteProduct = async (id) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 300));

  const product = localStorage.getItem(`product:${id}`);
  if (!product) {
    throw new Error('Không tìm thấy sản phẩm');
  }

  localStorage.removeItem(`product:${id}`);
  return { success: true };
};

export const searchProducts = async (query) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 300));

  const products = Object.values(getProducts());
  if (!query) return products;

  const searchTerm = query.toLowerCase();
  return products.filter(product => 
    product.name.toLowerCase().includes(searchTerm) ||
    product.description.toLowerCase().includes(searchTerm)
  );
};

export const getProductsByCategory = async (category) => {
  // Simulate API delay
  await new Promise(resolve => setTimeout(resolve, 300));

  const products = Object.values(getProducts());
  if (!category || category === 'all') return products;

  return products.filter(product => product.category === category);
};