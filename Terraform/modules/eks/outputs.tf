output "cluster_name" {
  value = module.eks.cluster_name
}

output "cluster_endpoint" {
  value = module.eks.cluster_endpoint
}

output "node_group_names" {
  value = keys(module.eks.eks_managed_node_groups)
}