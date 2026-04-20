import os

def replace_in_file(path, old, new):
    with open(path, 'r') as f:
        content = f.read()
    if old in content:
        content = content.replace(old, new)
        with open(path, 'w') as f:
            f.write(content)
        print(f"Updated: {path}")
    else:
        print(f"Target not found in: {path}")

base = "/Users/bharathank/pjs/l&t_capstone/attendance-backend/src/main/java/com/edutech/attendance"

replace_in_file(f"{base}/controller/EmployeeController.java", 
                '''@PreAuthorize("hasRole('EMPLOYEE')")''', 
                '''@PreAuthorize("hasAnyRole('EMPLOYEE', 'MANAGER')")''')

replace_in_file(f"{base}/config/SecurityConfig.java", 
                '''.requestMatchers("/employee/**").hasRole("EMPLOYEE")''', 
                '''.requestMatchers("/employee/**").hasAnyRole("EMPLOYEE", "MANAGER")''')
