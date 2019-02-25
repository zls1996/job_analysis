from selenium import webdriver
import re
import time
import csv
import selenium.webdriver.support.expected_conditions as EC
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.common.by import By
import lxml.html
from selenium.common.exceptions import TimeoutException
from selenium.webdriver.chrome.options import Options
#from selenium import JavascriptExecutor
etree=lxml.html.etree
import time
import io  
import sys
import random
import os
from file_util import *
import string
import gc

heads=['company','position_name','salary',
    'address','work_years','education','job_desp']
path='D:\Software\Google\Chrome\Application\chromedriver.exe'
chrome_options=Options()
driver=webdriver.Chrome(executable_path=path)
kinds=[['互联网_电子商务','https://search.51job.com/list/000000,000000,0000,\
    01%252C37%252C32,9,99,%2B,2,1.html\
    ?lang=c&stype=1&postchannel=0000&workyear=99&cotype=\
    99&degreefrom=03%2C04%2C05%2C06&jobterm=01&companysize=\
    99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&fromType=\
    22&dibiaoid=0&address=&line=&specialarea=00&from=&welfare='],
        ['金融','https://search.51job.com/list/000000,000000,0000,\
        03%252C41,9,99,%2B,2,1.html?lang=c&stype=1&postchannel=0000&\
        workyear=99&cotype=99&degreefrom=03%2C04%2C05%2C06&jobterm=01&\
        companysize=99&lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&\
        fromType=22&dibiaoid=0&address=&line=&specialarea=00&from=&welfare='],
        ['房地产','https://search.51job.com/list/000000,000000,0000,26,9,99,\
        %2B,2,1.html?lang=c&stype=1&postchannel=0000&workyear=99&cotype=99&\
        degreefrom=03%2C04%2C05%2C06&jobterm=01&companysize=99&\
        lonlat=0%2C0&radius=-1&ord_field=0&confirmdate=9&\
        fromType=22&dibiaoid=0&address=&line=&specialarea=00&\
        from=&welfare=']]

class Job51Spider:
    def __init__(self,driver):
        self.browser=driver
        self.data_path="./data_of_51job"
        self.log_name="./log_51job.txt"
        create_dir(self.data_path)
        create_file(self.log_name)
        log=self.get_log()
        self.kind_num=int(log[0])
        self.page_num=int(log[1])

    def get_log(self):
        with open(self.log_name,'r') as log_file:
            data=log_file.read()
            res=[]
            if not data:
                res=['0','1']
            else:
                res=data.split(',')
            log_file.close()
            return res

    def update_log(self):
        with open(self.log_name,'w+') as log_file:
            log_file.write(str(self.kind_num)+','+str(self.page_num))
            log_file.close()

    def crawl_job_detail(self,url,positions):
        self.browser.execute_script('window.open("%s")'%url)
        self.browser.switch_to.window(self.browser.window_handles[2])      
        try:
            job=[]
            WebDriverWait(driver=self.browser,timeout=10).until(
                    EC.presence_of_element_located((By.XPATH,
                        "//div[@class='tCompanyPage']")))
            source=self.browser.page_source
            html=etree.HTML(source)
            company_name=html.xpath("//p[@class='cname']/a/@title")
            job_name=html.xpath("//div[@class='cn']/h1/@title")
            salary=html.xpath("//div[@class='cn']/strong/text()")
            title=html.xpath("//p[@class='msg ltype']/@title")
            job_desp=html.xpath("//div[@class='tCompany_main']/\
                div[@class='tBorderTop_box'][1]//text()")
            title_contents=''.join(title).split('|')
            address=title_contents[0].strip()
            split_index=address.find('-')
            if split_index!=-1:
                address=address[0:split_index]
            work_years=title_contents[1].strip()
            edu=title_contents[2].strip()
            job_info="".join(job_desp)
            #job_info=job_desp[0]
            job_info=job_info.replace('\n','')
            job_info=job_info.replace('\t','')
            job_info=job_info.replace(' ','')
            job_info.strip()
            split_index=job_info.find('职能类别')
            if split_index!=-1:
                job_info=job_info[0:split_index]
            if salary:
                job.append(company_name[0])
                job.append(job_name[0])
                job.append(salary[0])
                job.append(address)
                job.append(work_years)
                job.append(edu)
                job.append(job_info)
                positions.append(job)
        except:
            print("error on get detail",flush=True)
        try:
            self.browser.close()
        except:
            print("error on closeing",flush=True)
        self.browser.switch_to.window(self.browser.window_handles[1])

    def crawl_page(self,positions):
        items=self.browser.find_elements_by_class_name('el')
        item_urls=[]
        for item in items:
            span=item.find_element_by_tag_name('span')
            innerSource=span.get_attribute('innerHTML')
            html=etree.HTML(innerSource)
            link=html.xpath("//a/@href")
            if link:
                item_urls.append(link[0]) 
        
        for url in item_urls:
            if url.startswith('https://jobs.51job.com') or url.startswith('http://jobs.51job.com'):
                self.crawl_job_detail(url,positions)
                time.sleep(random.randint(2,5))

    def jump_to_page(self):
        self.browser.maximize_window()
        input=self.browser.find_element_by_id('jump_page')
        input.clear()
        input.send_keys(str(self.page_num))
        #//*[@id="resultList"]/div[55]/div/div/div/span[3]
        #button=self.browser.find_element_by_class_name('og_but')
        #button
        #JavascriptExecutor js = (JavascriptExecutor)self.browser;
        #js.executeScript("searchMenu()");
        #js = 'document.getElementsByClassName("og_but")[0].click();'
        self.browser.execute_script("jumpPage('2000')")


    def crawl_url(self,kind,url):
        self.browser.execute_script('window.open("%s")'%url)
        self.browser.switch_to.window(self.browser.window_handles[1])
        filename=self.data_path+"/"+kind+".xlsx"
        if not os.path.exists(filename):
            create_file(filename)
            write_xlsx_header(filename,heads)
        try:
            WebDriverWait(driver=self.browser,timeout=10).until(
                        EC.presence_of_element_located((By.XPATH,
                            "//div[@class='dw_page']")))
        except:
            print('error on waiting page',flush=True)
            return
        if self.page_num!=1:
            self.jump_to_page()
        loop_count=0
        while True:
            try:
                WebDriverWait(driver=self.browser,timeout=10).until(
                        EC.presence_of_element_located((By.XPATH,
                            "//div[@class='dw_page']")))
            except:
                print('error on waiting page',flush=True)
                break
            positions=[]
            print("爬取:"+kind+" page:"+str(self.page_num),flush=True)
            self.crawl_page(positions)
            #next page to be crawled
            self.page_num+=1
            if positions:
                write_xlsx_data(filename,positions)
            #test if the page were the last page
            if self.page_num>=2001:
                self.kind_num+=1
                self.page_num=1
                break
            #update log
            self.update_log()
            loop_count+=1
            if loop_count%10==0:
                gc.collect()
            #try to enter the next page;break if any exception occurs
            try:
                #//*[@id="resultList"]/div[55]/div/div/div
                 next_page=self.browser.find_element_by_link_text('下一页')
                 next_page.click()
            except:
                print("something wrong!")
                break
        try:
            self.browser.close()
        except:
            print('error on closing in crawl_url')
        self.browser.switch_to.window(self.browser.window_handles[0])

    def run(self):
        kinds_len=len(kinds)
        for i in range(self.kind_num,kinds_len):
            kind=kinds[i]
            self.crawl_url(kind[0],kind[1])
            self.update_log()
        


if __name__=="__main__":
    sys.stdout = io.TextIOWrapper(sys.stdout.buffer,encoding='utf8')
    driver.get("https://www.51job.com")
    job51Spider=Job51Spider(driver)
    job51Spider.run()