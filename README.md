# AWS DYNAMODB
[Guide](https://aws.amazon.com/blogs/database/amazon-dynamodb-single-table-design-using-dynamodbmapper-and-spring-boot/)

Tip: Don't use IAM User credentials on EC2 unless you absolutely have to. Instead, launch the EC2 instance with an appropriate IAM Role (or edit the instance later to apply an IAM Role). The SDK will then automatically retrieve credentials and they will be auto-rotated for you (unlike IAM User credentials, which are fixed and hence your exposure is greater) [Using an IAM role to grant permissions to apps](https://docs.aws.amazon.com/IAM/latest/UserGuide/id_roles_use_switch-role-ec2.html?icmpid=docs_iam_console).
For local test the IAM User Credential could be acceptable even though we have [AWS Toolkit integrated in the IDE](https://aws.amazon.com/developer/tools/#IDE_and_IDE_Toolkits) as an alternative.

If you're not using AWS Toolkit make sure you configure the following ENVIRONMENT VARIABLES:
AWS_ACCESS_KEY_ID= {KEY}
AWS_SECRET_ACCESS_KEY= {SECRET}

SAM

    Package: sam package --template-file ./s3-deployment.yaml --output-template-file ./s3-deployment-out.yaml --region us-east-1 
    Deploy: sam deploy --template-file ./s3-deployment-out.yaml --stack-name s3-deployment-stack --region us-east-1

    Package: sam package --template-file ./eb-deployment.yaml --output-template-file ./eb-deployment-out.yaml --s3-bucket eb-s3 --region us-east-1
    Deploy: sam deploy --template-file ./eb-deployment-out.yaml --stack-name eb-deployment-stack --region us-east-1 --capabilities CAPABILITY_NAMED_IAM

    Package: sam package --template-file ./dynamodb-deployment.yaml --output-template-file ./dynamodb-deployment-out.yaml  --region us-east-1
    Deploy: sam deploy --template-file ./dynamodb-deployment-out.yaml --stack-name <YOUR STACK NAME>

    Package: sam package --template-file ./code-pipeline-deployment.yaml --output-template-file ./code-pipeline-deployment-out.yaml  --region us-east-1
    Deploy: sam deploy --template-file ./code-pipeline-deployment-out.yaml --stack-name <YOUR STACK NAME>