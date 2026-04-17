#!/usr/bin/env python3
import os
import re

def add_getters_setters(file_path):
    """Add getters and setters to a Java POJO file"""
    with open(file_path, 'r') as f:
        content = f.read()
    
    # Extract field declarations
    field_pattern = r'private\s+(\w+)\s+(\w+);'
    fields = re.findall(field_pattern, content)
    
    if not fields:
        return
    
    # Build getters and setters
    getters_setters = []
    for field_type, field_name in fields:
        # Capitalize first letter for getter/setter
        capitalized = field_name[0].upper() + field_name[1:]
        
        getter = f'''
    public {field_type} get{capitalized}() {{
        return {field_name};
    }}'''
        
        setter = f'''
    public void set{capitalized}({field_type} {field_name}) {{
        this.{field_name} = {field_name};
    }}'''
        
        getters_setters.append(getter + setter)
    
    # Add before closing brace
    methods_str = '\n'.join(getters_setters)
    new_content = content.replace('}', f'{methods_str}\n}}', 1)
    
    with open(file_path, 'w') as f:
        f.write(new_content)

# Process all DTO and Entity files
dto_dir = '/Users/bharathank/pjs/l&t_capstone/attendance-backend/src/main/java/com/edutech/attendance/dto'
entity_dir = '/Users/bharathank/pjs/l&t_capstone/attendance-backend/src/main/java/com/edutech/attendance/entity'

for directory in [dto_dir, entity_dir]:
    for file in os.listdir(directory):
        if file.endswith('.java'):
            file_path = os.path.join(directory, file)
            print(f"Processing {file_path}")
            add_getters_setters(file_path)

print("Done!")
