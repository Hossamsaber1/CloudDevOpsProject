# Troubleshooting Guide

# 1. Namespace Not Found

## Error

```text
namespaces "ivolve" not found
```

---

## Cause

Deployment manifests were applied before namespace creation.

---

## Solution

Apply namespace first:

```bash
kubectl apply -f k8s/base/namespace.yaml
```

Then apply remaining manifests:

```bash
kubectl apply -f k8s/base/
```

---

# 2. Jenkins Cannot Find AWS CLI

## Error

```text
aws: not found
```

---

## Cause

AWS CLI was not installed on Jenkins EC2 instance.

---

## Solution

Installed AWS CLI automatically using Ansible role.

---

# 3. ECR Push Failed

## Error

```text
repository does not exist
```

---

## Cause

Mismatch between:

- Terraform ECR repository name
- Jenkins ECR repository variable

---

## Solution

Updated Jenkins variable:

```groovy
ECR_REPO = 'clouddevopsproject-repo'
```

---

# 4. Git Push Failed Inside Jenkins

## Error

```text
src refspec dev does not match any
```

---

## Cause

Jenkins workspace was in detached HEAD state.

---

## Solution

Added:

```bash
git checkout -B dev
```

before pushing changes.

---

# 5. Kubernetes CrashLoopBackOff

## Error

```text
CrashLoopBackOff
```

---

## Cause

Application container runs on:

```text
5000
```

while Kubernetes deployment expected:

```text
80
```

---

## Solution

Updated deployment:

```yaml
containerPort: 5000
```

Updated service:

```yaml
targetPort: 5000
```

---

# 6. ArgoCD Application Progressing

## Cause

AWS Load Balancer Controller was missing.

---

## Solution

Installed:

- OIDC Provider
- IAM Policy
- IAM Service Account
- AWS Load Balancer Controller

---

# 7. AWS Load Balancer Controller CrashLoopBackOff

## Error

```text
failed to get VPC ID
```

---

## Cause

Controller failed to detect VPC automatically using EC2 metadata.

---

## Solution

Installed controller using explicit VPC ID:

```bash
--set vpcId=vpc-08e87d2072fca98d0
```

---

# 8. Ingress ADDRESS Empty

## Cause

AWS Load Balancer Controller was not operational.

---

## Solution

Fixed:

- IAM configuration
- Controller installation
- VPC configuration

---

# 9. Helm Installation Timeout

## Error

```text
context deadline exceeded
```

---

## Cause

Temporary EKS API timeout.

---

## Solution

Re-ran Helm installation command successfully.

---

# Verification Commands

## Verify Pods

```bash
kubectl get pods -n ivolve -o wide
```

---

## Verify Ingress

```bash
kubectl get ingress -n ivolve
```

---

## Verify ArgoCD

```bash
kubectl get applications -n argocd
```

---

## Verify Load Balancer Controller

```bash
kubectl get pods -n kube-system | grep aws-load-balancer
```

---

# Final Status

Successfully achieved:

- Working Jenkins CI/CD pipeline
- Kubernetes deployment
- GitOps continuous deployment
- AWS ALB public access
- ArgoCD synchronization
- Amazon EKS deployment