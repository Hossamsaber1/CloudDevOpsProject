# CloudDevOpsProject Documentation

# Project Overview

CloudDevOpsProject is a complete end-to-end Cloud DevOps implementation on AWS using modern DevOps and GitOps practices.

The project demonstrates:

- Infrastructure as Code (IaC)
- Configuration Management
- CI/CD Automation
- Containerization
- Kubernetes Orchestration
- GitOps Continuous Deployment

The implementation uses:

- Terraform
- Ansible
- Docker
- Jenkins
- Amazon EKS
- Amazon ECR
- Kubernetes
- ArgoCD
- AWS Load Balancer Controller
- GitHub

---

# Project Objectives

The main objective of this project is to build a production-style DevOps workflow on AWS.

The project includes:

- Automated infrastructure provisioning
- Automated Jenkins server configuration
- Containerized application deployment
- CI/CD pipeline implementation
- Kubernetes orchestration
- GitOps deployment model
- Public application exposure using AWS ALB

---

# Architecture Diagram

![Project Architecture](screenshot/project-architecture.png)

```text
Developer
   ↓
GitHub Repository
   ↓
Jenkins CI Pipeline
   ↓
Docker Image Build
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
Deploy to Amazon EKS
   ↓
AWS Load Balancer Controller
   ↓
AWS Application Load Balancer
   ↓
Public Application Access
```

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

## Objective

Create a GitHub repository for the DevOps project.

## Repository

```text
CloudDevOpsProject
```

## Branch Strategy

```text
main
dev
```

Development operations were performed on the `dev` branch.

---

# Step 2 — Docker Containerization

## Objective

Containerize the Flask application using Docker.

---

# Dockerfile Responsibilities

The Dockerfile performs:

- Base image setup
- Dependency installation
- Application copy
- Runtime configuration
- Gunicorn startup

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

## Objective

Provision AWS infrastructure using reusable Terraform modules.

---

# Terraform Modules

## Network Module

Responsible for creating:

- VPC
- Public Subnets
- Private Subnets
- NAT Gateway
- Internet Gateway
- Route Tables

---

## Server Module

Responsible for creating:

- Jenkins EC2 instance
- Security Groups

---

## EKS Module

Responsible for creating:

- Amazon EKS Cluster
- Worker Nodes
- Node Groups

---

## ECR Module

Responsible for creating:

- Amazon Elastic Container Registry Repository

---

# Terraform Backend

# AWS Infrastructure

## Amazon S3 Backend

![Amazon S3 Backend](screenshot/aws/aws-s3-backend.png)

---

## DynamoDB State Locking

![DynamoDB](screenshot/aws/aws-dynamodb-locks.png)

---

## Amazon EKS Cluster

![Amazon EKS Cluster](screenshot/aws/aws-eks-cluster.png)

---

## EKS Node Group

![EKS Node Group Active](screenshot/aws/aws-eks-nodegroup-active.png)

Terraform remote state backend configured using:

```text
Amazon S3
```

---

# Terraform Commands

## Initialize Terraform

```bash
terraform init
```

## Plan Infrastructure

```bash
terraform plan
```

# Terraform Infrastructure

## Terraform Plan

![Terraform Plan](screenshot/terraform/terraform-plan.png)

---

## Terraform Apply

![Terraform Apply](screenshot/terraform/terraform-apply-01.png)

![Terraform Apply 02](screenshot/terraform/terraform-apply-02.png)

```bash
terraform apply
```

---

# Step 4 — Configuration Management with Ansible

## Objective

Automate Jenkins EC2 configuration.

---

# Installed Components

Ansible roles installed:

- Java
- Jenkins
- Docker
- Trivy
- AWS CLI
- Git

---

# Dynamic Inventory

AWS EC2 Dynamic Inventory used for automatic host discovery.

---

# Execute Playbook

# Jenkins Configuration

## Jenkins Installation

![Jenkins Installation](screenshot/jenkins/jenkins-installation.png)

---

## Jenkins Admin User

![Jenkins Admin User](screenshot/jenkins/jenkins-admin-user.png)

---

## Jenkins URL Configuration

![Jenkins URL](screenshot/jenkins/jenkins-url-config.png)

```bash
ANSIBLE_CONFIG=$PWD/ansible.cfg ansible-playbook playbooks/jenkins.yml
```

---

# Step 5 — Kubernetes Orchestration

## Objective

Deploy the application into Amazon EKS cluster.

---

# Kubernetes Namespace

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

# Kubernetes Verification

## kubectl get nodes

![kubectl get nodes](screenshot/kubernetes/kubectl-get-nodes.png)

---

## kubectl get all

![kubectl get all](screenshot/kubernetes/kubectl-get-all.png)

---

## kubectl get pods

![kubectl get pods](screenshot/kubernetes/kubectl-get-pods.png)

---

## kubectl get ingress

![kubectl get ingress](screenshot/kubernetes/kubectl-get-ingress.png)

## Verify Pods

```bash
kubectl get pods -n ivolve -o wide
```

## Verify Services

```bash
kubectl get svc -n ivolve
```

## Verify Ingress

```bash
kubectl get ingress -n ivolve
```

---

# Step 6 — Jenkins Continuous Integration Pipeline

## Objective

Implement CI pipeline using Jenkins.

---



# Jenkins Pipeline Results

## Successful Pipeline

![Pipeline Success](screenshot/jenkins/jenkins-pipeline-success.png)

---

## Pipeline Failure Example

![Pipeline Failure](screenshot/jenkins/jenkins-pipeline-failed.png)
## 1. Build Image

Build Docker image.

---

## 2. Scan Image

Scan image using Trivy.

---

## 3. Push Image

Push image to Amazon ECR.

---

## 4. Delete Local Image

Remove local image after push.

---

## 5. Update Kubernetes Manifests

Update deployment image tag.

---

## 6. Push Manifest Changes

Push updated manifests back to GitHub repository.

---

# Jenkins Shared Library

Reusable pipeline functions stored inside:

```text
vars/
```

---

# Step 7 — Continuous Deployment with ArgoCD

## Objective

Implement GitOps continuous deployment.

---

# ArgoCD Responsibilities

ArgoCD continuously:

- Monitors GitHub repository
- Detects manifest changes
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

- OIDC Provider
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

# Verify AWS Load Balancer Controller

```bash
kubectl get pods -n kube-system | grep aws-load-balancer
```

---

# Public Application Access

Application exposed publicly through:

```text
AWS Application Load Balancer (ALB)
```

---

# ArgoCD Dashboard

## ArgoCD Login

![ArgoCD Login](screenshot/argocd/argocd-login.png)

---

## ArgoCD Dashboard

![ArgoCD Dashboard](screenshot/argocd/argocd-dashboard.png)

```bash
kubectl get applications -n argocd
```

Expected:

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

Scans executed during Jenkins pipeline.

---

# GitOps Workflow

```text
Developer
→ GitHub
→ Jenkins CI
→ Amazon ECR
→ Manifest Update
→ Git Push
→ ArgoCD Sync
→ Amazon EKS Deployment
```

---

# Application Deployment

## Application Homepage

![Application Homepage](screenshot/application/application-homepage.png)

---

## Live Application

![Live Application](screenshot/application/application-live-01.png)

![Live Application 02](screenshot/application/application-live-02.png)

---

## Curl Validation

![Curl Validation](screenshot/application/curl-validation.png)

Successfully implemented:

- AWS Infrastructure Automation
- CI/CD Pipeline
- Kubernetes Deployment
- GitOps Continuous Deployment
- Public Application Access
- Automated Image Security Scanning

---

# Author

Hossam Saber

Visualization & Render Systems Engineer  
Cloud DevOps Engineer