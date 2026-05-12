variable "project_name" {}
variable "vpc_id" {}

variable "cluster_subnet_ids" {
  type = list(string)
}

variable "private_subnet_ids" {
  type = list(string)
}