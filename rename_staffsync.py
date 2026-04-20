import os
import glob

# 1. Update index.html
index_file = '/Users/bharathank/pjs/l&t_capstone/angular-frontend/src/index.html'
if os.path.exists(index_file):
    with open(index_file, 'r') as f:
        c = f.read()
    c = c.replace('<title>AngularFrontend</title>', '<title>StaffSync - Modern HR Management</title>')
    c = c.replace('<title>AttendanceFrontend</title>', '<title>StaffSync - Modern HR Management</title>')
    with open(index_file, 'w') as f:
        f.write(c)

# 2. Update HTML pages
html_files = glob.glob('/Users/bharathank/pjs/l&t_capstone/angular-frontend/src/app/**/*.html', recursive=True)
for p in html_files:
    with open(p, 'r') as f:
        html = f.read()
    
    # Navbrand updates with FontAwesome Logo
    html = html.replace('>Admin Portal<', '><i class="fa-solid fa-layer-group me-2 text-info"></i> StaffSync Admin<')
    html = html.replace('Employee Portal', '<i class="fa-solid fa-users me-2 text-info"></i> StaffSync Employee')
    html = html.replace('Manager Portal', '<i class="fa-solid fa-network-wired me-2 text-info"></i> StaffSync Manager')
    
    # Login & Register Title
    html = html.replace('Attendance Management System', 'StaffSync')
    html = html.replace('Login to your account', '<i class="fa-solid fa-bolt text-warning me-2"></i>StaffSync Platform')
    
    # Premium card replacements
    html = html.replace('card shadow-sm border-0', 'card premium-card')
    html = html.replace('card shadow border-0', 'card premium-card')
    html = html.replace('card shadow-sm', 'card premium-card')

    # Remove bg-light from premium cards header to make it look smooth
    html = html.replace('card-header bg-light', 'card-header bg-transparent border-bottom')
    
    with open(p, 'w') as f:
        f.write(html)

print("Branding and premium card classes applied globally!")
