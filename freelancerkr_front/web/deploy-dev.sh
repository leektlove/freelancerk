../gradlew bootJar
../gradlew build
scp -i ../freelancerk.pem build/libs/web-0.0.1-SNAPSHOT.jar ec2-user@15.164.231.45:~/
ssh -i ../freelancerk.pem ec2-user@15.164.231.45 'sh deploy-dev.sh'
