#!/usr/bin/env python3
"""
Script to fix security test files - replace setters with constructors for DTOs
"""

import re
import os

def fix_login_request(content):
    """Fix LoginRequest instantiation"""
    # Pattern for special cases like null username/password
    content = re.sub(
        r'LoginRequest (\w+) = new LoginRequest\(\);\s+\1\.setUserName\(null\);',
        r'// LoginRequest \1 with null username - need manual fix\n        LoginRequest \1 = new LoginRequest(null, "password");',
        content
    )
    content = re.sub(
        r'LoginRequest (\w+) = new LoginRequest\(\);\s+\1\.setPassword\(null\);',
        r'// LoginRequest \1 with null password - need manual fix\n        LoginRequest \1 = new LoginRequest("username", null);',
        content
    )
    
    # Pattern 1: LoginRequest with setUserName and setPassword on same lines
    pattern1 = r'LoginRequest (\w+) = new LoginRequest\(\);\s+\1\.setUserName\("([^"]*)"\);\s+\1\.setPassword\("([^"]*)"\);'
    replacement1 = r'LoginRequest \1 = new LoginRequest("\2", "\3");'
    content = re.sub(pattern1, replacement1, content)
    
    # Pattern 2: Handle line-by-line with more care
    lines = content.split('\n')
    i = 0
    while i < len(lines):
        match = re.match(r'(\s*)LoginRequest (\w+) = new LoginRequest\(\);', lines[i])
        if match and i + 2 < len(lines):
            indent = match.group(1)
            varname = match.group(2)
            
            # Check next two lines
            line1 = lines[i+1].strip()
            line2 = lines[i+2].strip() if i+2 < len(lines) else ""
            
            username_match = re.match(rf'{varname}\.setUserName\((.*)\);', line1)
            password_match = re.match(rf'{varname}\.setPassword\((.*)\);', line2)
            
            if username_match and password_match:
                username = username_match.group(1)
                password = password_match.group(1)
                lines[i] = f'{indent}LoginRequest {varname} = new LoginRequest({username}, {password});'
                del lines[i+1:i+3]
                continue
        i += 1
    content = '\n'.join(lines)
    
    return content

def fix_create_product_request(content):
    """Fix CreateProductRequest instantiation"""
    lines = content.split('\n')
    i = 0
    while i < len(lines):
        match = re.match(r'(\s*)CreateProductRequest (\w+) = new CreateProductRequest\(\);', lines[i])
        if match and i + 5 < len(lines):
            indent = match.group(1)
            varname = match.group(2)
            
            # Collect all setters
            j = i + 1
            setters = {}
            while j < len(lines) and lines[j].strip().startswith(f'{varname}.set'):
                setter_match = re.match(rf'{indent}{varname}\.set(\w+)\((.*)\);', lines[j])
                if setter_match:
                    field = setter_match.group(1)
                    value = setter_match.group(2)
                    setters[field] = value
                    j += 1
                else:
                    break
            
            # Check if we have all required fields
            if 'ProductName' in setters and 'Price' in setters and 'Quantity' in setters and 'Category' in setters:
                description = setters.get('Description', 'null')
                new_line = f'{indent}CreateProductRequest {varname} = new CreateProductRequest({setters["ProductName"]}, {setters["Price"]}, {description}, {setters["Quantity"]}, {setters["Category"]});'
                lines[i] = new_line
                del lines[i+1:j]
        i += 1
    content = '\n'.join(lines)
    return content

def fix_update_product_request(content):
    """Fix UpdateProductRequest instantiation"""
    lines = content.split('\n')
    i = 0
    while i < len(lines):
        match = re.match(r'(\s*)UpdateProductRequest (\w+) = new UpdateProductRequest\(\);', lines[i])
        if match and i + 5 < len(lines):
            indent = match.group(1)
            varname = match.group(2)
            
            # Collect all setters
            j = i + 1
            setters = {}
            while j < len(lines) and lines[j].strip().startswith(f'{varname}.set'):
                setter_match = re.match(rf'{indent}{varname}\.set(\w+)\((.*)\);', lines[j])
                if setter_match:
                    field = setter_match.group(1)
                    value = setter_match.group(2)
                    setters[field] = value
                    j += 1
                else:
                    break
            
            # Check if we have all required fields
            if 'ProductName' in setters and 'Price' in setters and 'Quantity' in setters and 'Category' in setters:
                description = setters.get('Description', 'null')
                new_line = f'{indent}UpdateProductRequest {varname} = new UpdateProductRequest({setters["ProductName"]}, {setters["Price"]}, {description}, {setters["Quantity"]}, {setters["Category"]});'
                lines[i] = new_line
                del lines[i+1:j]
        i += 1
    content = '\n'.join(lines)
    return content

def fix_category_enum(content):
    """Fix Category.ELECTRONICS.getValue() to just "Electronics" """
    content = re.sub(r'Category\.ELECTRONICS\.getValue\(\)', '"Electronics"', content)
    content = re.sub(r'Category\.BOOKS\.getValue\(\)', '"Books"', content)
    content = re.sub(r'Category\.CLOTHING\.getValue\(\)', '"Clothing"', content)
    content = re.sub(r'Category\.TOYS\.getValue\(\)', '"Toys"', content)
    content = re.sub(r'Category\.GROCERIES\.getValue\(\)', '"Groceries"', content)
    
    # Also fix direct enum usage (should be string)
    content = re.sub(r'\.setCategory\(Category\.ELECTRONICS\)', '.setCategory("Electronics")', content)
    content = re.sub(r'\.setCategory\(Category\.BOOKS\)', '.setCategory("Books")', content)
    content = re.sub(r'\.setCategory\(Category\.CLOTHING\)', '.setCategory("Clothing")', content)
    content = re.sub(r'\.setCategory\(Category\.TOYS\)', '.setCategory("Toys")', content)
    content = re.sub(r'\.setCategory\(Category\.GROCERIES\)', '.setCategory("Groceries")', content)
    
    return content

def process_file(filepath):
    """Process a single Java test file"""
    print(f"Processing {filepath}...")
    
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()
    
    original_content = content
    
    # Apply all fixes
    content = fix_login_request(content)
    content = fix_create_product_request(content)
    content = fix_update_product_request(content)
    content = fix_category_enum(content)
    
    # Only write if changed
    if content != original_content:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"  ✓ Fixed {filepath}")
        return True
    else:
        print(f"  - No changes needed for {filepath}")
        return False

def main():
    security_dir = r'e:\FloginFE_BE\backend\src\test\java\com\flogin\security'
    
    test_files = [
        'SqlInjectionTest.java',
        'XssSecurityTest.java',
        'CsrfSecurityTest.java',
        'AuthenticationBypassTest.java',
        'InputValidationTest.java',
        'SecurityBestPracticesTest.java'
    ]
    
    fixed_count = 0
    for filename in test_files:
        filepath = os.path.join(security_dir, filename)
        if os.path.exists(filepath):
            if process_file(filepath):
                fixed_count += 1
        else:
            print(f"WARNING: File not found: {filepath}")
    
    print(f"\nDone! Fixed {fixed_count} files.")

if __name__ == '__main__':
    main()
