rm -rf /home/sruthi/Downloads/apache-tomcat-6.0.43/webapps/heatmapserver /home/sruthi/Downloads/apache-tomcat-6.0.43/webapps/heatmapserver.war
echo "Removed from tomcat webapps" 
ant servlet-war
mv heatmapserver.war /home/sruthi/Downloads/apache-tomcat-6.0.43/webapps
