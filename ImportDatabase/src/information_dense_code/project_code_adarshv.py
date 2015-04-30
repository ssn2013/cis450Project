import pickle
import time
import math
import os
from liblinearutil import *
from lxml import etree
from subprocess import Popen
from collections import Counter
import re
from os import listdir


def load_collection_tokens(directory):
        dir_list=[]
        fileSet=get_all_files(os.path.join(directory))
        for filename in fileSet:
              dir_list.extend(load_xmlfile_tokens(filename))
        return dir_list


def get_all_files(directory):
    result = []
    for root, dnames, file_names in os.walk(directory):                                     # Get the filnames recursively which are relative to the given directory
        for filename in file_names:
            result.append(os.path.join(root,filename))
    return(result)

def function1():
                                if not os.path.exists('train_xml'):
                                    os.makedirs('train_xml/')
				if not os.path.exists('test_xml'):
					os.makedirs('test_xml/')
                                #os.system('ls -d -1 /home1/c/cis530/project/train_data/*.* > train_file_list.txt')
				os.system('ls -d -1 /home1/c/cis530/project/test_data/*.* > test_file_list.txt')
                                os.system('java -cp /home1/c/cis530/hw3/corenlp/stanford-corenlp-2012-07-09/stanford-corenlp-2012-07-09.jar:/home1/c/cis530/hw3/corenlp/stanford-corenlp-2012-07-09/stanford-corenlp-2012-07-06-models.jar:/home1/c/cis530/hw3/corenlp/stanford-corenlp-2012-07-09/xom.jar:/home1/c/cis530/hw3/corenlp/stanford-corenlp-2012-07-09/joda-time.jar -Xmx3g edu.stanford.nlp.pipeline.StanfordCoreNLP -annotators tokenize,ssplit,pos,lemma,ner,parse -filelist test_file_list.txt -outputDirectory test_xml/')

def train_word2vec():
        train_dir='/home1/c/cis530/project/train_data/'
        f=open('word_file.txt','w')
        words=load_collection_tokens(train_dir)
        for x in words:
                f.write(x+' ')
        f.close()
        tp="/project/cis/nlp/tools/word2vec/word2vec -train word_file.txt -output vec.txt -cbow 0 -size 200 -window 5 -negative 0 -hs 1 -sample 1e-3 -threads 12"
        os.system(tp)



def extract_top_words(xml_directory):
				stop = []
                                co = 0
                                top_words = []
                                words = []
                                filenames = os.listdir(xml_directory)
                                for temp in filenames:
                                                tree = etree.parse(xml_directory+temp)
                                                root = tree.getroot()
                                                for token in root.iter('token'):
                                                        words.append(token[0].text.lower())
                                                co = co + 1

                                di = Counter(words)
                                c=0
                                for k in sorted(di, key=di.get, reverse=True):
                                        if c == 3000:
                                                break
                                        else:
                                                top_words.append(k)
                                                c=c+1


                                return top_words


def map_unigrams(xml_filename, top_words):
                                output_list = []
                                words = []
                                for w in etree.parse(xml_filename).xpath("//sentence"):
                                      words.append(w.xpath(".//word/text()"))
                                flat = [x for sublist in words for x in sublist]
                                flat = [x.lower() for x in flat]
                                for w in top_words:
                                        if w in flat:
                                                output_list.append(1)
                                        else:
                                                output_list.append(0)

                                return output_list

def cosine_similarity(X,Y):
                                 num = 0
                                 deno1 = 0
                                 deno2 = 0
                                 final = 0
                                 val1 = all(el==0 for el in X)
                                 val2 = all(el==0 for el in Y)
                                 if(val1):
                                        return 0
                                 elif(val2):
                                        return 0
                                 else:
                                        for i in range(0,X.__len__()):
                                                num = float(num) + (float(X[i])*float(Y[i]))
                                                deno1 = float(deno1) + (float(X[i])*float(X[i]))
                                                deno2 = float(deno2) + (float(Y[i])*float(Y[i]))
                                 deno2 = math.sqrt(deno2)
                                 deno1 = math.sqrt(deno1)
                                 final = float(num) / (deno1*deno2)
                                 return final


def extract_similarity(top_words):
                                final = {}
                                di = {}
                                temp = []
                                temp1 = []
                                actual_words = []
                                actual_list = []
                                words = []
                                c  = 0
                                with open('/home1/a/adarshv/project/vec.txt') as f:
                                        for line in f:
                                                words = line.split()
                                                temp.append(words[0])
                                                words.pop(0)
                                                temp1.append(words)
                                for i in range(0,len(temp)):
                                        if temp[i] in top_words:
                                                actual_words.append(temp[i])
                                                actual_list.append(temp1[i])
                                for i in range(0,len(actual_words)):
                                        for j in range(0,len(actual_words)):
                                                if actual_words[i] != actual_words[j]:
                                                        value = cosine_similarity(actual_list[i],actual_list[j])
                                                        if value != 0:
                                                                di[actual_words[j]] = value
                                        final[actual_words[i]] = di
                                        di = {}

                                return final




def map_expanded_unigrams(xml_file, top_words, similarity_matrix):
                                final = []
                                uni = map_unigrams(xml_file,top_words)
                                non_zero = {}
                                zero = {}
                                m = 0
                                X = []
                                Y = []
                                for i in range(0,len(top_words)):
                                        if uni[i] == 1:
                                                non_zero[top_words[i]] = 1
                                        else:
                                                zero[top_words[i]] = 0

                                for z in zero.keys():
                                        for nz in non_zero.keys():
                                             if z in similarity_matrix and nz in similarity_matrix[z]:
                                                 value = similarity_matrix[z][nz]
                                                 if(value > m):
                                                        m=value
                                        zero[z] = m
                                        m = 0

                                for x in top_words:
                                        if x in zero:
                                                final.append(zero[x])
                                        else:
                                                final.append(1)

                                return final



def extract_top_dependencies(xml_directory):
                                 tup = ()
                                 final = []
                                 top_tups = []
                                 c = 0
                                 co = 0
                                 for ro, dnames, file_names in os.walk(xml_directory):
                                        for filename in file_names:
                                                co = co + 1

                                                temp=os.path.join(ro,filename)
                                                root = etree.parse(temp)
                                                for target in root.findall("//basic-dependencies"):
                                                        l1 = target.xpath(".//dep/@type")
                                                        l1 = [x.lower() for x in l1]
                                                        l2 = target.xpath(".//governor/text()")
                                                        l2 = [x.lower() for x in l2]
                                                        l3 = target.xpath(".//dependent/text()")
                                                        l3 = [x.lower() for x in l3]
                                                        for i in range(0,len(l1)):
                                                                tup = (l1[i],l2[i],l3[i])
                                                                final.append(tup)


                                 di = Counter(final)
                                 for k in sorted(di, key=di.get, reverse=True):
                                        if c == 3000:
                                                break
                                        else:
                                                top_tups.append(k)
                                                c=c+1

                                 return top_tups


def map_dependencies(xml_filename, dependency_list):
                                tup = ()
                                final = []
                                root = etree.parse(xml_filename)
                                map_dep = []
                                for target in root.findall("//basic-dependencies"):
                                          l1 = target.xpath(".//dep/@type")
                                          l1 = [x.lower() for x in l1]
                                          l2 = target.xpath(".//governor/text()")
                                          l2 = [x.lower() for x in l2]
                                          l3 = target.xpath(".//dependent/text()")
                                          l3 = [x.lower() for x in l3]
                                          for i in range(0,len(l1)):
                                                  tup = (l1[i],l2[i],l3[i])
                                                  final.append(tup)

                                for i in range(0,len(dependency_list)):
                                        if dependency_list[i] in final:
                                                map_dep.append(1)
                                        else:
                                                map_dep.append(0)



                                return map_dep



def extract_prod_rules(xml_directory):
        prod_rules = []
        ww = 0
        for root, dnames, file_names in os.walk(xml_directory):
           for filename in file_names:

                         temp=os.path.join(root,filename)
                         with open(temp) as f:
                          for st in re.findall('<parse>(.*?)</parse>', f.read(), re.S):
                           i =0
                           lis = []
                           temp = ''
                           final = []
                           while(i<len(st)):
                                if(st[i] == '('):
                                        lis.append(st[i])
                                        i=i+1
                                if(st[i] == ')'):
                                        lis.append(st[i])
                                        i=i+1
                                else:
                                        if(st[i-1]!=' '):
                                                while(st[i]!= ' '):
                                                        temp = temp+st[i]
                                                        i=i+1
                                                lis.append(temp)
                                                temp = ''
                                                i=i+1
                                        else:
                                                while(st[i]!=')'):
                                                        i=i+1
                           lis = [x for x in lis if x]
                           k=0
                           f1 = []
                           for i in range(0,len(lis)):
                                if(lis[i] == '('):
                                        if i == 0:
                                                final.append(lis[i+1])
                                                k = k+1
                                        else:
                                                temp = final[k-1]
                                                temp = temp + '_'+lis[i+1]
                                                final[k-1] = temp
                                                final.append(lis[i+1])
                                                k=k+1


                                elif(lis[i] == ')'):
                                        f1.append(final[len(final)-1])
                                        final.pop()
                                        k=k-1
                           f2 = []

                           for i in range(0,len(f1)):
                                if '_' in f1[i]:
                                        f2.append(f1[i])
                           prod_rules = prod_rules + f2

                           k=0
                           f1 = []
                           f2=[]
                           final = []
                           lis = []
        di = Counter(prod_rules)
        c = 0
        top_prod_rules  = []
        for k in sorted(di, key=di.get, reverse=True):
                if c == 3000:
                     break
                else:
                     top_prod_rules.append(k)
                     c=c+1

        return top_prod_rules

def get_mi(bg_terms, topic_terms):
        import math
        lst=[]
        ret_lst=[]
        for w in topic_terms:
                if w not in ret_lst:
                        if topic_terms.count(w) >= 5:
                                temp1=(float)(topic_terms.count(w))/len(topic_terms)
                                temp2=float(bg_terms.count(w))/len(bg_terms)
                                p=math.log(temp1/temp2)
                                if p < 0:
                                        p=0
                                lst.append([w,p])
                                ret_lst.append(w)
        return lst



def get_mi_top(bg_terms, topic_terms, k):
        ret_lst=[]
        lst_tfidf=get_mi(bg_terms,topic_terms)
        lst_tfidf=sorted(lst_tfidf, key=lambda (x,y): y, reverse=True)
        for i in enumerate(lst_tfidf):
                ret_lst.append(i[1][0])
        if k < 0:
                return ret_lst
        return ret_lst[0:k]


def create_mi_vector(id_values,train_data):
	info_d=[]
	info_s=[]
	topic_terms_d=[]
	topic_terms_s=[]
	files=get_all_files(train_data)
	for i in files:
		k=i.split('/')
		if id_values[k[len(k)-1]]=='1':
			topic_terms_d.extend(load_xmlfile_tokens(i))
		else:
			topic_terms_s.extend(load_xmlfile_tokens(i))
	print len(topic_terms_d)
	print len(topic_terms_s)
	bg_terms=load_collection_tokens(train_data)
	top_d=get_mi_top(bg_terms, topic_terms_d, 500)		
	top_s=get_mi_top(bg_terms, topic_terms_s, 500)		
	return (top_d,top_s)


def prod_rules_helper(xml_filename):
         prod_rules = []
         with open(xml_filename) as f:
                          for st in re.findall('<parse>(.*?)</parse>', f.read(), re.S):
                           i =0
                           lis = []
                           temp = ''
                           final = []
                           while(i<len(st)):
                                if(st[i] == '('):
                                        lis.append(st[i])
                                        i=i+1
                                if(st[i] == ')'):
                                        lis.append(st[i])
                                        i=i+1
                                else:
                                        if(st[i-1]!=' '):
                                                while(st[i]!= ' '):
                                                        temp = temp+st[i]
                                                        i=i+1
                                                lis.append(temp)
                                                temp = ''
                                                i=i+1
                                        else:
                                                while(st[i]!=')'):
                                                        i=i+1
                           lis = [x for x in lis if x]
                           k=0
                           f1 = []
                           for i in range(0,len(lis)):
                                if(lis[i] == '('):
                                        if i == 0:
                                                final.append(lis[i+1])
                                                k = k+1
                                        else:
                                                temp = final[k-1]
                                                temp = temp + '_'+lis[i+1]
                                                final[k-1] = temp
                                                final.append(lis[i+1])
                                                k=k+1


                                elif(lis[i] == ')'):
                                        f1.append(final[len(final)-1])
                                        final.pop()
                                        k=k-1
                           f2 = []

                           for i in range(0,len(f1)):
                                if '_' in f1[i]:
                                        f2.append(f1[i])
                           prod_rules = prod_rules + f2

                           k=0
                           f1 = []
                           f2=[]
                           final = []
                           lis = []
         return prod_rules



def map_prod_rules(xml_filename, rules_list):
        final = []
	prod_rules = prod_rules_helper(xml_filename)
        for i in range(0, len(rules_list)):
                        if rules_list[i] in prod_rules:
                                final.append(1)
                        else:
                                final.append(0)

        return final

def load_xmlfile_tokens(input_xml):
       import xml.etree.ElementTree as ET
       words=[]
       tree = ET.parse(input_xml)
       root = tree.getroot()
       for token in root.iter('token'):
            words.append(token[0].text.lower())
       return words

def map_MRC(fil,MRC):
	words = load_xmlfile_tokens(fil)
	length = len(words)
	di = Counter(words)
	final = []
	for x in MRC:
		if di.has_key(x):
			val = float(di[x])/length
			final.append(val)
		else:
			final.append(0)
	return final
	


def process_corpus( xml_dir, top_words, top_dependencies, prod_rules,top_mi):
        files=get_all_files(xml_dir)
        if 'test' in xml_dir:
                f1=open("test_1.txt",'w')
        else:
                f1=open("train_1.txt",'w')
	for f in files:
		result2=map_unigrams(f,top_words)
		result3=map_dependencies(f, top_dependencies)
		result4=map_prod_rules(f,prod_rules)
		result5=map_unigrams(f,top_mi)
		f=f.split('/')
		f=f[len(f)-1]
		f1.write('{} '.format(f))
		list1=[]
		list2=[]
		list3=[]
		list4=[]
		k=''
		j=''
		l=''
		p=''
		for i in range(1,3001):	
			if result2[i-1]!=0:
                               
                                f1.write('{}:{} '.format(i,result2[i-1]))
			if result3[i-1]!=0:
				q=i+3000
				j=j+str(q)+":"+str(result3[i-1])+' '
			if result4[i-1]!=0:
				q=i+6000
				l=l+str(q)+":"+str(result4[i-1])+' '
		for i in range(1,1001):
			if result5[i-1]!=0:
                                q=i+9000
                                p=p+str(q)+":"+str(result4[i-1])+' '	
		
		k=j+l+p
		f1.write(k)
		f1.write('\n');
	f1.close()




def LIBLINEAR_format_file(input_file,output_file,id_values):
        o=open(output_file,'w')
        i=open(input_file,'r')
        for line in i:
                lin=line.split()
                if id_values.has_key(lin[0]):
                        line=line.replace(lin[0],id_values[lin[0]])
                else:
                        line=line.replace(lin[0],'1')
                o.write('{}'.format(line))
        o.close()
        i.close()




def get_precision(L_1, L_2):
        num = 0
        num1 = 0
        de = 0
        de1 = 0
        for x in range(0,len(L_1)):
                if L_1[x] == 1:
                        de = de+1
                if L_1[x] == 1 and L_2[x] == 1:
                        num = num + 1
                if L_1[x] == -1:
                        de1 = de1 + 1
                if L_1[x] == -1 and L_2[x] == -1:
                        num1 = num1 + 1
        pre = float(num) / de
        pre1 = float(num1) / de1
        return (pre, pre1)

def get_recall(L_1,L_2):
        num = 0
        num1 = 0
        de = 0
        de1 = 0
        for x in range(0,len(L_1)):
                if L_2[x] == 1:
                        de = de+1
                if L_1[x] == 1 and L_2[x] == 1:
                        num = num + 1
                if L_2[x] == -1:
                        de1 = de1 + 1
                if L_1[x] == -1 and L_2[x] == -1:
                        num1 = num1 + 1
        pre = float(num) / de
        pre1 = float(num1) / de1
        return (pre,pre1)


def get_fmeasure(L_1,L_2):
        precision = get_precision(L_1,L_2)
        recall = get_recall(L_1,L_2)                                            # Function to calculate the F-measure parameter
        f = float((2*precision[0]*recall[0])/(precision[0]+recall[0]))
        f1 = float((2*precision[1]*recall[1])/(precision[1]+recall[1]))
        return (f,f1)



def run_classifier(train_file,test_file):
        A_in_train=0
        B_in_train=0
        A_in_test=0
        B_in_test=0
        X=[]
        Y=[]
        tr=open(train_file,'r')
        te=open(test_file,'r')
        for line in tr.readlines():
                line=line.split()
                if line[0]=='1':
                        A_in_train=A_in_train+1
                else:
                        B_in_train=B_in_train+1
                X.append(line[0])
                line=line[1:len(line)]
                Y.append(line)
        tr.close()
        A_train=float(A_in_train)/len(X)
        B_train=float(B_in_train)/len(X)


        y, x = svm_read_problem(train_file)
        prob = problem(y,x)
        arg='-s 0 -w1 '+str(B_train)+' -w-1 '+str(A_train)
        arg=parameter(arg)
        m = train(prob, arg)
        y, x = svm_read_problem(test_file)
        p_label, p_acc, p_val = predict(y, x, m,'-b 1')
        A_in_predic=0
        B_in_predic=0

        X=[]
        for line in te.readlines():
                line=line.split()
                X.append(int(line[0]))
        te.close()
        prob_1=[]
        index=0
        if m.label[0]==-1:
                index=1
        else:
                index=0
        for i in p_val:
                prob_1.append(i[index])

        return p_label

def extract_MRC_words(fil):
	words = []
	i = 0
	f = open(fil,'r')
	for line in f:
		words.append(line.strip('\n'))
	f.close()
	return words
		


def check_accuracy(p_label,test_file,id_values):
        X=[]
        total=0.0
        size=len(p_label)
        te=open(test_file,'r')
        for line in te.readlines():
                line=line.split()
                X.append(int(id_values[line[0]]))
        te.close()
        for i in range(len(X)):
                if p_label[i] == X[i]:
                        total=total+1
        acc=(total*100)/size
	return acc


'''
Main to execute all the functions
train_xml/ contains the Stanford Core NLP processed xml training files 
test_xml/ contains the Stanford Core NLP processed xml test files 

train_data='train_xml/'
test_data='test_xml/'
id_values={}
f=open('train_labels.txt','r')
for line in f:
        	line=line.split()
                l=str(line[0])
                l=l.replace('txt','xml')
                id_values[l]=line[1]
f.close()
top_mi_d=create_mi_vector(id_values,train_data)
top_mi=top_mi_d[0].extend(top_mi_d[1])
top_mi=top_mi_d[0]

prod_list = extract_prod_rules(train_data)
top_words = extract_top_words(train_data)
top_dep = extract_top_dependencies(train_data)

process_corpus( train_data,top_words, top_dep, prod_list, top_mi)
LIBLINEAR_format_file('train_1.txt','liblinear_train_1',id_values)
process_corpus(test_data, top_words, top_dep, prod_list, top_mi)
LIBLINEAR_format_file('test_1.txt','liblinear_test_1',id_values)
p_label=run_classifier('liblinear_train_1','liblinear_test_1')

f = open('test_1.txt','r')
f1 = open('test.txt','w')
i =0
for line in f:
	words = line.split()
	wo = words[0]
	f1.write(wo.replace('.xml','.txt')+' '+str(int(p_label[i])))
	f1.write('\n')
	i = i +1 
'''	




