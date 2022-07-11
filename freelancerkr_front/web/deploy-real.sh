../gradlew bootJar
../gradlew build
scp -i ../freelancerk.pem build/libs/web-0.0.1-SNAPSHOT.jar ec2-user@13.124.183.202:~/
ssh -i ../freelancerk.pem ec2-user@13.124.183.202 'sh deploy.sh'
