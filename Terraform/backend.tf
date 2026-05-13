terraform {
  backend "s3" {
    bucket         = "clouddevopsproject-tfstate-hossam"
    key            = "terraform/state.tfstate"
    region         = "eu-north-1"
    dynamodb_table = "clouddevopsproject-terraform-locks"
    encrypt        = true
  }
}