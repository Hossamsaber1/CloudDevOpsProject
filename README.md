# CloudDevOpsProject

## End-to-End Cloud DevOps Implementation on AWS

CloudDevOpsProject is a complete production-style DevOps implementation designed to demonstrate modern Cloud DevOps practices using AWS infrastructure and Kubernetes GitOps workflows.

The project covers the full DevOps lifecycle including:

- Infrastructure Provisioning
- Configuration Management
- Containerization
- Continuous Integration
- Kubernetes Orchestration
- Continuous Deployment
- GitOps Automation
- AWS Cloud Integration
- Security Scanning

---

# Project Objectives

The main goal of this project is to build a scalable and automated DevOps environment using industry-standard tools and best practices.

This project demonstrates:

- Infrastructure as Code (IaC)
- CI/CD Pipeline Automation
- Kubernetes Deployment Automation
- GitOps Continuous Deployment
- Secure Container Delivery
- Cloud Infrastructure Management

---

# Technologies Used

| Category | Technology |
|---|---|
| Cloud Provider | AWS |
| Infrastructure as Code | Terraform |
| Configuration Management | Ansible |
| CI/CD | Jenkins |
| Containerization | Docker |
| Container Registry | Amazon ECR |
| Container Orchestration | Kubernetes (Amazon EKS) |
| GitOps | ArgoCD |
| Security Scanning | Trivy |
| Version Control | Git & GitHub |
| Load Balancer | AWS Application Load Balancer |
| Package Manager | Helm |

---

# Project Architecture

```text
Developer
   ↓
GitHub Repository
   ↓
Jenkins Pipeline
   ↓
Build Docker Image
   ↓
Trivy Security Scan
   ↓
Push Image to Amazon ECR
   ↓
Update Kubernetes Manifest
   ↓
Push Changes to GitHub
   ↓
ArgoCD Detects Changes
   ↓
Amazon EKS Cluster
   ↓
AWS Load Balancer Controller
   ↓
AWS Application Load Balancer (ALB)
   ↓
Public Application Access
```

---

# Infrastructure Architecture

## Terraform Modules

The infrastructure was provisioned using reusable Terraform modules.

### Network Module

Creates:

- VPC
- Public Subnets
- Private Subnets
- NAT Gateway
- Internet Gateway
- Route Tables
- Network ACLs

---

### Server Module

Creates:

- Jenkins EC2 Instance
- Security Groups

---

### EKS Module

Creates:

- Amazon EKS Cluster
- Managed Node Groups
- Multi-AZ Worker Nodes

---

### ECR Module

Creates:

- Amazon Elastic Container Registry Repository

---

# Project Structure

```text
CloudDevOpsProject/
│
├── Terraform/
│   ├── modules/
│   │   ├── network/
│   │   ├── server/
│   │   ├── eks/
│   │   └── ecr/
│   │
│   ├── provider.tf
│   ├── backend.tf
│   ├── variables.tf
│   ├── outputs.tf
│   └── terraform.tfvars
│
├── ansible/
│   ├── playbooks/
│   ├── roles/
│   ├── inventories/
│   └── ansible.cfg
│
├── app/
│   └── Dockerfile
│
├── k8s/
│   └── base/
│       ├── namespace.yaml
│       ├── deployment.yaml
│       ├── service.yaml
│       └── ingress.yaml
│
├── vars/
│   ├── buildImage.groovy
│   ├── scanImage.groovy
│   ├── pushImage.groovy
│   ├── deleteImage.groovy
│   ├── updateManifests.groovy
│   └── pushManifests.groovy
│
├── argocd/
│   └── application.yaml
│
├── docs/
│   ├── 08-project-documentation.md
│   └── troubleshooting.md
│
├── Jenkinsfile
└── README.md
```

---

# Step 1 — GitHub Repository Setup

Repository:

```text
CloudDevOpsProject
```

Branch strategy:

```text
main
dev
```

Development and CI/CD operations were executed on the `dev` branch.

---

# Step 2 — Docker Containerization

## Objective

Containerize the Flask application using Docker.

---

# Build Docker Image

```bash
docker build -t clouddevopsproject .
```

---

# Run Docker Container

```bash
docker run -p 5000:5000 clouddevopsproject
```

---

# Step 3 — Terraform Infrastructure Provisioning

## Initialize Terraform

```bash
cd Terraform
terraform init
```

---

## Plan Infrastructure

```bash
terraform plan
```

---

## Apply Infrastructure

```bash
terraform apply
```

---

# AWS Resources Created

- VPC
- Public & Private Subnets
- NAT Gateway
- Internet Gateway
- Jenkins EC2
- Security Groups
- Amazon EKS Cluster
- Amazon ECR Repository

---

# Step 4 — Configuration Management with Ansible

## Objective

Automate Jenkins EC2 configuration.

---

# Installed Packages

- Java
- Jenkins
- Docker
- Trivy
- AWS CLI
- Git

---

# Execute Playbook

```bash
cd ansible

ANSIBLE_CONFIG=$PWD/ansible.cfg \
ansible-playbook playbooks/jenkins.yml
```

---

# Dynamic Inventory

AWS EC2 Dynamic Inventory was configured for automatic Jenkins host discovery.

---

# Step 5 — Kubernetes Orchestration

## Kubernetes Namespace

```text
ivolve
```

---

# Kubernetes Resources

## Deployment

Configured with:

- 2 replicas
- Pod anti-affinity
- Multi-node scheduling

---

## Service

Configured as:

```text
ClusterIP
```

---

## Ingress

Configured using:

```text
AWS ALB Ingress Controller
```

---

# Deploy Kubernetes Resources

```bash
kubectl apply -f k8s/base/
```

---

# Verify Kubernetes Deployment

## Verify Nodes

```bash
kubectl get nodes
```

---

## Verify Pods

```bash
kubectl get pods -n ivolve -o wide
```

---

## Verify Services

```bash
kubectl get svc -n ivolve
```

---

## Verify Ingress

```bash
kubectl get ingress -n ivolve
```

---

# Step 6 — Jenkins Continuous Integration Pipeline

## Pipeline Stages

### 1. Build Image

Build Docker image using Jenkins.

---

### 2. Scan Image

Scan Docker image using Trivy.

---

### 3. Push Image

Push image to Amazon ECR.

---

### 4. Delete Local Image

Remove local Docker image after successful push.

---

### 5. Update Kubernetes Manifest

Update deployment image tag automatically.

---

### 6. Push Manifest Changes

Push updated manifests back to GitHub repository.

---

# Jenkins Shared Library

Reusable pipeline functions stored inside:

```text
vars/
```

---

# Jenkins Credentials

Configured credentials:

- AWS_CREDENTIALS
- GITHUB_CREDENTIALS
- AWS_ACCOUNT_ID

---

# Step 7 — Continuous Deployment with ArgoCD

## Objective

Implement GitOps continuous deployment.

---

# ArgoCD Responsibilities

ArgoCD continuously:

- Monitors GitHub repository
- Detects Kubernetes manifest changes
- Synchronizes EKS cluster automatically

---

# Install ArgoCD

```bash
kubectl create namespace argocd

kubectl apply --server-side -n argocd \
-f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml
```

---

# ArgoCD Application

Application manifest location:

```text
argocd/application.yaml
```

---

# AWS Load Balancer Controller

Installed using:

- IAM Policy
- IAM Service Account
- Helm

---

# Install AWS Load Balancer Controller

```bash
helm install aws-load-balancer-controller eks/aws-load-balancer-controller \
  -n kube-system \
  --set clusterName=clouddevopsproject-eks \
  --set serviceAccount.create=false \
  --set serviceAccount.name=aws-load-balancer-controller \
  --set region=eu-north-1 \
  --set vpcId=vpc-08e87d2072fca98d0
```

---

# Verify Load Balancer Controller

```bash
kubectl get pods -n kube-system | grep aws-load-balancer
```

---

# Public Application Access

Application exposed publicly through:

```text
AWS Application Load Balancer (ALB)
```

Example:

```text
http://<ALB-DNS>
```

---

# Verify ArgoCD

```bash
kubectl get applications -n argocd
```

Expected status:

```text
Synced Healthy
```

---

# Security

## Image Security Scanning

Implemented using:

```text
Trivy
```

Security scans executed during Jenkins pipeline execution.

---

# GitOps Workflow

```text
Developer
→ GitHub
→ Jenkins CI
→ Amazon ECR
→ Update Kubernetes Manifests
→ Git Push
→ ArgoCD Sync
→ Amazon EKS Deployment
```

---

# Troubleshooting

Detailed troubleshooting guide available at:

```text
docs/troubleshooting.md
```

---

# Final Result

Successfully implemented:

- Infrastructure as Code
- Automated CI/CD Pipeline
- Kubernetes Orchestration
- GitOps Continuous Deployment
- Public AWS ALB Access
- Secure Container Delivery
- Amazon EKS Production Workflow

---

# Future Improvements

- Monitoring with Prometheus & Grafana
- Centralized Logging
- HTTPS with ACM
- Route53 Domain Integration
- Horizontal Pod Autoscaling
- Multi-Environment Deployment
- Blue-Green Deployment
- Kubernetes Secrets Management

---

# Author

Hossam Saber

Visualization & Render Systems Engineer  
Cloud DevOps Engineer

GitHub:

```text
https://github.com/Hossamsaber1
```