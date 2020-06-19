import os
import fileinput
import html


ctr = 0
"""
for subdir, dirs, files in os.walk('.'):
    for file in files:
        #print os.path.join(subdir, file)
        filepath = subdir + os.sep + file

        if not filepath.endswith(".py") and not filepath.endswith(".txt") and not ".git\\" in filepath:
            ctr += 1
            if ctr % 100 == 0:
                print(ctr)
            with fileinput.FileInput(filepath, openhook=fileinput.hook_encoded("utf-8")) as f:
                for line in f:
                    line.replace('&lt;', '<')
                    line.replace('&gt;', '>')
                    
            
"""

for subdir, dirs, files in os.walk('.'):
    for file in files:
        #print os.path.join(subdir, file)
        filepath = subdir + os.sep + file

        if not filepath.endswith(".py") and not filepath.endswith(".txt") and not ".git\\" in filepath:
            # Read in the file
            try:
                with open(filepath, 'r', encoding="utf-8") as f1 :
                  filedata = f1.read()
              
                filedata = html.unescape(filedata)


                # Write the file out again
                with open(filepath, 'w', encoding="utf-8") as f2:
                  f2.write(filedata)
            except:
                print(filepath)
    
