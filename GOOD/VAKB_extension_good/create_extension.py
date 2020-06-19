import os
import requests
from os import path
import time

headers = {'User-Agent': 'Mozilla/5.0 (Windows NT 6.3; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2049.0 Safari/537.36',
       'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8',
       'Accept-Charset': 'ISO-8859-1,utf-8;q=0.7,*;q=0.3',
       'Accept-Encoding': 'none',
       'Accept-Language': 'en-US,en;q=0.8',
       'Connection': 'keep-alive'}


projects_file = open('projects.txt', 'r')

link_start = 'https://www.programcreek.com/java-api-examples/?code='
line_ctr = 0

for line in projects_file:
    line_ctr += 1
    print("Starting on line: " + str(line_ctr))
    slash_split = line.split('/')
    project_code = slash_split[4][6:]
    project_name = slash_split[5]
    project_root = slash_split[6]

    done = False
    attempt = 0
    while (not done):
        #try:
        attempt += 1
        with requests.get(line.rstrip(), headers=headers) as response:
            html = response.content.decode('utf-8')
            treeview_index = html.find('<div id="treeview"')
            treeview_end_index = html.find('</div>', treeview_index)
            
            while (True):
                span_index = html.find('<span href', treeview_index)
                if (span_index == -1 or span_index > treeview_end_index):
                    break;
                
                link_end_index = html.find('">', span_index)
                link_split = html[span_index:link_end_index].split('/')[3:]
                if (len(link_split) == 1 and '\\' in link_split[0]):
                    link_split = link_split[0].split('\\')
                    
                filename = link_split[-1]
                print("Starting: " + filename)
                print(link_split)
                directories = project_root
                for directory in link_split[:len(link_split) - 1]:
                    directories = os.path.join(directories, directory)
                        
                os.makedirs(directories, exist_ok=True)
                link = link_start + project_code + "/" + project_name + "/" + project_root + "/" + "/".join(link_split)
                new_filepath = os.path.join(directories, filename)
                print("Filepath is: " + new_filepath)
                if (not path.exists(new_filepath)):
                    done2 = False
                    attempt2 = 0
                    while (not done2):
                        #try:
                        attempt2 += 1
                        with requests.get(link, headers=headers) as response2:
                            html2 = response2.content.decode('utf-8')
                            codeinner_index = html2.find('<div id="codeinner')
                            prettyprint_index = html2.find('prettyprint')
                            content_start = html2.find('>', prettyprint_index) + 1
                            content_end = html2.find('</pre>', content_start)
                            content = html2[content_start:content_end]
                            file = open(new_filepath, "w", encoding="utf-8")
                            file.write(content)
                            file.close()
                            print("Done: " + filename + '\n')
                        done2 = True
                        #except:
                        #    if (attempt2 > 10):
                        #        print("Sleeping 2!")
                        #        time.sleep(1)
                        #    continue
                            
                else:
                    print("\n")

                #Move to next limk
                treeview_index = span_index + 1
        done = True
        #except:
        #    if (attempt > 10):
        #        print("Sleeping!")
        #        time.sleep(1)
        #    continue
            


        
        
