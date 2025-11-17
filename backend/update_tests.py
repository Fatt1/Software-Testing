#!/usr/bin/env python3
"""
Script tự động cập nhật ProductServiceTest để sử dụng Bean Validation mocking.
"""

import re

# Định nghĩa mapping giữa error message và violation message
ERROR_MESSAGES = {
    "Product Name không được rỗng": "Product Name không được rỗng",
    "Product Name phải từ 3 đến 100 ký tự": "Product Name phải từ 3 đến 100 ký tự",
    "Price không được để trống": "Price không được để trống",
    "Price phải > 0": "Price phải > 0",
    "Price không được vượt quá 999999999": "Price không được vượt quá 999999999",
    "Quantity không được để trống": "Quantity không được để trống",
    "Quantity phải >= 0": "Quantity phải >= 0",
    "Quantity không được vượt quá 99999": "Quantity không được vượt quá 99999",
    "Description không được vượt quá 500 ký tự": "Description không được vượt quá 500 ký tự",
    "Category không được rỗng": "Category không được rỗng",
}

def should_add_validator_mock(test_content):
    """Kiểm tra xem test có cần thêm validator mock không"""
    # Nếu test đã có mockValidator.validate thì skip
    if "mockValidator.validate" in test_content:
        return False
    
    # Nếu test có createProduct hoặc updateProduct thì cần mock
    if "createProduct(" in test_content or "updateProduct(" in test_content:
        return True
    
    return False

def extract_error_message(test_content):
    """Trích xuất error message từ test"""
    match = re.search(r'contains\("([^"]+)"\)', test_content)
    if match:
        return match.group(1)
    return None

def generate_validator_mock(error_message):
    """Tạo code mock validator"""
    if error_message in ERROR_MESSAGES:
        return f'''
            // Mock validator trả về violation
            ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
            when(violation.getMessage()).thenReturn("{ERROR_MESSAGES[error_message]}");
            when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
'''
    return None

def add_validator_verify(test_content):
    """Thêm verify mockValidator vào test"""
    if "verify(mockValidator" in test_content:
        return test_content
    
    # Thêm verify mockValidator trước verify(productRepository
    if "verify(productRepository, never()).save(any())" in test_content:
        test_content = test_content.replace(
            "verify(productRepository, never()).save(any())",
            "verify(mockValidator, times(1)).validate(productDto);\n            verify(productRepository, never()).save(any())"
        )
    elif "verify(productRepository, times(1)).save(any(Product.class))" in test_content:
        test_content = test_content.replace(
            "verify(productRepository, times(1)).save(any(Product.class))",
            "verify(mockValidator, times(1)).validate(productDto);\n            verify(productRepository, times(1)).save(any(Product.class))"
        )
    
    return test_content

def add_validator_mock_for_success(test_content):
    """Thêm validator mock cho test cases thành công"""
    if "mockValidator.validate" in test_content:
        return test_content
    
    # Tìm vị trí sau "// Arrange" để insert mock
    if "// Arrange" in test_content and "createProduct(" in test_content:
        # Tìm dòng cuối cùng của Arrange section (thường là dòng khai báo Product hoặc ProductDto)
        lines = test_content.split('\n')
        arrange_index = next(i for i, line in enumerate(lines) if "// Arrange" in line)
        
        # Tìm dòng đầu tiên của Act section
        act_index = next((i for i, line in enumerate(lines) if "// Act" in line), None)
        
        if act_index:
            # Insert mock validator code trước Act section
            mock_code = "\n            // Mock validator trả về không có lỗi\n            when(mockValidator.validate(productDto)).thenReturn(Set.of());\n"
            lines.insert(act_index, mock_code)
            test_content = '\n'.join(lines)
    
    return test_content

# Thông báo
print("=" * 80)
print("SCRIPT HƯỚNG DẪN CẬP NHẬT PRODUCTSERVICETEST")
print("=" * 80)
print()
print("Script này cung cấp hướng dẫn về cách cập nhật test cases.")
print("Do độ phức tạp của cấu trúc code, nên cập nhật thủ công từng test.")
print()
print("PATTERN CẦN ÁP DỤNG:")
print("=" * 80)
print()
print("1. CHO TEST THÀNH CÔNG (không có lỗi validation):")
print("-" * 80)
print('''
        // Thêm sau phần Arrange, trước Act:
        when(mockValidator.validate(productDto)).thenReturn(Set.of());
        
        // Thêm vào phần Assert, trước verify repository:
        verify(mockValidator, times(1)).validate(productDto);
''')
print()
print("2. CHO TEST THẤT BẠI (có lỗi validation):")
print("-" * 80)
print('''
        // Thêm sau phần Arrange, trước Act:
        ConstraintViolation<ProductDto> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("Error message ở đây");
        when(mockValidator.validate(productDto)).thenReturn(Set.of(violation));
        
        // Thêm vào phần Assert, trước verify repository:
        verify(mockValidator, times(1)).validate(productDto);
''')
print()
print("3. CÁC ERROR MESSAGES CHUẨN:")
print("-" * 80)
for key, value in ERROR_MESSAGES.items():
    print(f'  - "{key}"')
print()
print("=" * 80)
print("LƯU Ý:")
print("  - Test GetProduct, DeleteProduct, GetAllProducts KHÔNG CẦN mock validator")
print("  - Chỉ test CreateProduct và UpdateProduct cần mock validator")
print("  - Category validation vẫn dùng Category.isValid() ngoài Bean Validation")
print("=" * 80)
