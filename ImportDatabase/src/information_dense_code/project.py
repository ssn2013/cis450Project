import os
f = open('train_labels.txt','r')

di = {}
lis = []
for line in f:
	lis = line.split()
	di[lis[0]] = lis[1]

f.close()
l1  = []
l2 = []

for i in range(1,11):
 f = open('set'+str(i)) 
 c1 = 0
 c2 = 0
 for line in f:
	line = line.strip('\n')
	filename = line.split('/')
	if(di[filename[-1]] == '1'):
		c1 = c1 + 1
	elif(di[filename[-1]] == '-1'):
		c2 = c2 + 1
 l1.append(c1)
 l2.append(c2)
 f.close()


sum1  = 0
sum2 = 0
for i in range(0,len(l1)):
	sum1=sum1 + l1[i]
	sum2=sum2 + l2[i]



res = open('weights','w')
for i in range(0,len(l1)):
	res.write(str(sum1-l1[i]))
	res.write(' ')
	res.write(str(sum2-l2[i]))
	res.write('\n')
res.close()

for i in range(1,11):
	os.mkdir('set_dir_'+str(i))	
	f = open('set'+str(i),'r')
	os.chdir('set_dir_'+str(i))
	for fil in f:	
		fil = fil.strip('\n')
		os.system('cp '+fil+' .')
	os.chdir('..')
	f.close()

	
		
