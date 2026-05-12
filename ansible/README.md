# Jenkins EC2 Configuration with Terraform + Ansible

## Overview

This folder contains the Ansible configuration management part for the same AWS Jenkins EC2 project provisioned with Terraform.

## Architecture

```text
Terraform  -> Creates AWS infrastructure
Ansible    -> Configures Jenkins EC2 instance
```

## What Terraform Should Create

- VPC
- Public Subnet
- Internet Gateway
- Route Table
- Security Group
- Jenkins EC2 Instance
- SSH Key Pair

## What Ansible Installs

- Java 17
- Jenkins
- Docker
- Trivy
- Required Linux packages

## Important Terraform Requirement

Your Jenkins EC2 instance must have this tag:

```hcl
tags = {
  Name = "Jenkins-Server"
  Role = "jenkins"
}
```

The Ansible Dynamic Inventory uses this tag to discover the Jenkins EC2 instance automatically.

## Required Security Group Ports

Your Terraform security group should allow:

```text
22    SSH
8080  Jenkins UI
```

## Project Structure

```text
.
├── ansible/
│   ├── ansible.cfg
│   ├── requirements.yml
│   ├── inventories/
│   │   └── aws_ec2.yml
│   ├── playbooks/
│   │   └── jenkins.yml
│   └── roles/
│       ├── common/
│       ├── docker/
│       ├── jenkins/
│       └── trivy/
└── terraform-example/
    ├── jenkins_ec2_tag_example.tf
    └── security_group_ports_example.tf
```

## Step 1: Run Terraform

From your Terraform directory:

```bash
terraform init
terraform plan
terraform apply
```

## Step 2: Configure AWS Credentials for Ansible

Ansible Dynamic Inventory needs AWS credentials.

Example:

```bash
aws configure
```

Or use environment variables:

```bash
export AWS_ACCESS_KEY_ID="your-access-key"
export AWS_SECRET_ACCESS_KEY="your-secret-key"
export AWS_DEFAULT_REGION="us-east-1"
```

On Windows PowerShell:

```powershell
$env:AWS_ACCESS_KEY_ID="your-access-key"
$env:AWS_SECRET_ACCESS_KEY="your-secret-key"
$env:AWS_DEFAULT_REGION="us-east-1"
```

## Step 3: Edit Region

Open:

```text
ansible/inventories/aws_ec2.yml
```

Make sure the region matches the Terraform AWS region:

```yaml
regions:
  - us-east-1
```

## Step 4: Edit SSH Key Path

Open:

```text
ansible/ansible.cfg
```

Change this line to your real `.pem` key:

```ini
private_key_file = ~/.ssh/jenkins-key.pem
```

## Step 5: Install Ansible Collections

```bash
cd ansible
ansible-galaxy collection install -r requirements.yml
```

## Step 6: Test Dynamic Inventory

```bash
ansible-inventory -i inventories/aws_ec2.yml --graph
```

Expected group:

```text
@jenkins_servers:
  <EC2_PUBLIC_IP>
```

## Step 7: Run Ansible Playbook

```bash
ansible-playbook -i inventories/aws_ec2.yml playbooks/jenkins.yml
```

## Step 8: Verify Jenkins EC2

SSH into the instance:

```bash
ssh -i ~/.ssh/jenkins-key.pem ubuntu@EC2_PUBLIC_IP
```

Check installed tools:

```bash
java -version
jenkins --version
docker --version
trivy --version
systemctl status jenkins
systemctl status docker
```

Get Jenkins initial password:

```bash
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

Open Jenkins:

```text
http://EC2_PUBLIC_IP:8080
```

## Troubleshooting

### Dynamic Inventory Does Not Show EC2

Check that the EC2 instance has:

```text
Role = jenkins
```

Check the AWS region in:

```text
ansible/inventories/aws_ec2.yml
```

### SSH Fails

Check:

- Security Group allows port 22
- Correct `.pem` file path in `ansible.cfg`
- Correct EC2 username: `ubuntu`
- EC2 is in a public subnet
- EC2 has a public IP

### Jenkins UI Does Not Open

Check:

- Security Group allows port 8080
- Jenkins service is running

```bash
sudo systemctl status jenkins
```

### Docker Permission Issue in Jenkins

Run:

```bash
sudo usermod -aG docker jenkins
sudo systemctl restart jenkins
```

