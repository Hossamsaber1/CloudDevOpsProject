# Cloud DevOps Project — AWS Infrastructure Provisioning with Terraform

## Project Overview

This project demonstrates a Production-Style DevOps Infrastructure deployment on AWS using Terraform modules.

The infrastructure includes:

- Custom VPC
- Public & Private Subnets
- Internet Gateway
- NAT Gateway
- Network ACLs
- Jenkins EC2 Server
- AWS ECR Repository
- Amazon EKS Cluster
- EKS Managed Worker Nodes
- Terraform Remote Backend using S3 & DynamoDB

---

# Architecture

```text
Internet
   │
   ▼
Internet Gateway
   │
   ▼
Public Subnets
   ├── NAT Gateway
   └── Jenkins EC2

Private Subnets
   └── EKS Worker Nodes

Amazon EKS Control Plane
Amazon ECR
Terraform S3 Backend
DynamoDB State Locking
```

---

# Technologies Used

| Technology | Purpose |
|---|---|
| Terraform | Infrastructure as Code |
| AWS VPC | Networking |
| AWS EC2 | Jenkins Server |
| AWS EKS | Kubernetes Cluster |
| AWS ECR | Docker Image Registry |
| S3 Backend | Terraform Remote State |
| DynamoDB | Terraform State Locking |
| IAM | Permissions & Roles |

---

# Project Structure

```text
terraform/
├── backend.tf
├── provider.tf
├── variables.tf
├── outputs.tf
├── terraform.tfvars
├── main.tf
└── modules/
    ├── network/
    │   ├── main.tf
    │   ├── variables.tf
    │   └── outputs.tf
    ├── server/
    │   ├── main.tf
    │   ├── variables.tf
    │   └── outputs.tf
    ├── ecr/
    │   ├── main.tf
    │   ├── variables.tf
    │   └── outputs.tf
    └── eks/
        ├── main.tf
        ├── variables.tf
        └── outputs.tf
```

---

# Step 1 — Configure AWS CLI

Install AWS CLI:

```powershell
aws --version
```

Configure credentials:

```powershell
aws configure
```

Example:

```text
AWS Access Key ID:
AWS Secret Access Key:
Default region name: eu-north-1
Default output format: json
```

Verify:

```powershell
aws sts get-caller-identity
```

---

# Step 2 — Create Terraform Remote Backend

## Create S3 Bucket

```powershell
aws s3 mb s3://clouddevopsproject-tfstate-hossam --region eu-north-1
```

Enable Versioning:

```powershell
aws s3api put-bucket-versioning \
  --bucket clouddevopsproject-tfstate-hossam \
  --versioning-configuration Status=Enabled
```

Enable Encryption:

```powershell
aws s3api put-bucket-encryption \
  --bucket clouddevopsproject-tfstate-hossam \
  --server-side-encryption-configuration '{
    "Rules": [
      {
        "ApplyServerSideEncryptionByDefault": {
          "SSEAlgorithm": "AES256"
        }
      }
    ]
  }'
```

---

## Create DynamoDB Lock Table

```powershell
aws dynamodb create-table \
  --table-name clouddevopsproject-terraform-locks \
  --attribute-definitions AttributeName=LockID,AttributeType=S \
  --key-schema AttributeName=LockID,KeyType=HASH \
  --billing-mode PAY_PER_REQUEST \
  --region eu-north-1
```

---

# Step 3 — Configure Terraform Backend

## backend.tf

```hcl
terraform {
  backend "s3" {
    bucket         = "clouddevopsproject-tfstate-hossam"
    key            = "terraform/state.tfstate"
    region         = "eu-north-1"
    dynamodb_table = "clouddevopsproject-terraform-locks"
    encrypt        = true
  }
}
```

---

# Step 4 — Configure Terraform Provider

## provider.tf

```hcl
provider "aws" {
  region = var.aws_region
}
```

---

# Step 5 — Terraform Variables

## variables.tf

```hcl
variable "project_name" {
  type    = string
  default = "clouddevopsproject"
}

variable "aws_region" {
  type    = string
  default = "eu-north-1"
}
```

---

# Step 6 — Terraform tfvars

## terraform.tfvars

```hcl
allowed_ssh_cidr = "196.219.248.226/32"
allowed_web_cidr = "196.219.248.226/32"

key_name = "clouddevops-key"
```

---

# Step 7 — Network Module

The Network module provisions:

- VPC
- Public Subnets
- Private Subnets
- Internet Gateway
- NAT Gateway
- Route Tables
- Network ACLs

---

## Network Architecture

```text
VPC: 10.0.0.0/16

Public Subnet 1 : 10.0.1.0/24
Public Subnet 2 : 10.0.2.0/24

Private Subnet 1 : 10.0.3.0/24
Private Subnet 2 : 10.0.4.0/24
```

---

# Step 8 — ECR Module

The ECR module creates:

- Amazon ECR Repository
- Image scanning on push
- Docker image storage

Example:

```hcl
resource "aws_ecr_repository" "repo" {
  name                 = "${var.project_name}-repo"
  image_tag_mutability = "MUTABLE"

  image_scanning_configuration {
    scan_on_push = true
  }
}
```

---

# Step 9 — Server Module

The Server module provisions:

- Jenkins EC2 Instance
- Security Groups
- SSH Access
- Jenkins Web Access

Ports:

| Port | Purpose |
|---|---|
| 22 | SSH |
| 8080 | Jenkins |

---

# Step 10 — EKS Module

The EKS module provisions:

- Amazon EKS Cluster
- IAM Roles
- EKS Managed Node Group
- Worker Nodes in Private Subnets

---

# EKS Worker Nodes

```text
Worker Nodes: 2
Instance Type: t3.medium
Deployment: Private Subnets
Availability Zones: Multiple
```

---

# Step 11 — Terraform Commands

Initialize Terraform:

```powershell
terraform init
```

Validate:

```powershell
terraform validate
```

Format:

```powershell
terraform fmt
```

Preview Changes:

```powershell
terraform plan
```

Provision Infrastructure:

```powershell
terraform apply
```

Destroy Infrastructure:

```powershell
terraform destroy
```

---

# Terraform Outputs

```powershell
terraform output
```

Example Outputs:

```text
jenkins_public_ip
eks_cluster_name
ecr_repository_url
vpc_id
```

---

# Security Best Practices

Implemented security practices:

- Private Subnets for EKS Worker Nodes
- Security Group Restrictions
- S3 Encryption
- S3 Versioning
- DynamoDB State Locking
- IAM Least Privilege Concepts
- NAT Gateway for Private Internet Access

---

# Troubleshooting

## Issue — EKS Worker Nodes Failed to Join Cluster

### Error

```text
NodeCreationFailure: Instances failed to join the Kubernetes cluster
```

---

## Root Cause

The issue was caused by a restrictive Network ACL configuration on the private subnets.

The private Network ACL allowed inbound traffic only from the VPC CIDR range:

```hcl
cidr_block = var.vpc_cidr
```

Because Network ACLs are stateless, return traffic required during EKS worker node bootstrap was blocked.

As a result:

- Worker nodes failed to communicate with the EKS control plane
- Node bootstrap process failed
- EKS Node Group entered CREATE_FAILED state

---

## Solution

The private subnet Network ACL ingress rule was updated to allow the required bootstrap and return traffic:

```hcl
cidr_block = "0.0.0.0/0"
```

Updated configuration:

```hcl
resource "aws_network_acl" "private_nacl" {
  vpc_id = aws_vpc.main.id

  subnet_ids = aws_subnet.private[*].id

  ingress {
    rule_no    = 100
    protocol   = "-1"
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }

  egress {
    rule_no    = 100
    protocol   = "-1"
    action     = "allow"
    cidr_block = "0.0.0.0/0"
    from_port  = 0
    to_port    = 0
  }
}
```

---

## Result

After updating the Network ACL:

- Terraform replaced the failed node group
- EKS worker nodes successfully joined the cluster
- Infrastructure deployment completed successfully

---

# Key Learning

This issue demonstrated the practical difference between:

| Component | Type |
|---|---|
| Security Groups | Stateful |
| Network ACLs | Stateless |

EKS worker node bootstrap requires:

- Proper private subnet routing
- NAT Gateway internet access
- Correct Network ACL rules
- EKS control plane communication

---

# Production-Level Lessons Learned

- Network ACL misconfiguration can break Kubernetes bootstrap
- Private subnet design is critical for EKS
- NAT Gateway is required for private worker nodes
- Terraform tainted resources automatically recreate failed infrastructure
- Troubleshooting cloud networking is a core DevOps skill

---

# Future Improvements

Potential future enhancements:

- Jenkins CI/CD Pipeline
- Docker Build Automation
- Kubernetes Deployment Automation
- ArgoCD GitOps
- Monitoring with Prometheus & Grafana
- Trivy Security Scanning
- Helm Charts
- Route53 + HTTPS
- Terraform Workspaces

---

# Author

## Hossam Saber

Cloud DevOps Engineer | Infrastructure & Kubernetes Enthusiast

---

# Instructor

## Ibrahim Adel

DevOps Engineer & Instructor

