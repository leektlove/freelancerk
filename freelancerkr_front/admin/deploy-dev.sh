../gradlew bootJar
../gradlew build
scp -i ../freelancerk.pem build/libs/admin-0.0.1-SNAPSHOT.jar ec2-user@52.78.140.231:~/
ssh -i ../freelancerk.pem ec2-user@52.78.140.231 'sh deploy.sh'
