workspace "Online Shop" {

    model {
        archetypes {
            ui = container {
                technology "React"
                tag "UI"
            }

            backend = container {
                technology "Java"
                tag "Backend"
            }

            database = container {
                technology "Oracle"
                tag "Database"
            }
        }

        user = person "User"

        shop = softwareSystem "Online Shop" {
            catalog = group "Product Catalog Service" {
                catalog_backend = backend "Product Catalog Backend" {
                    tag "BoundedContext"
                    catalog_backend_adapter_primary_rest = component "Primary Adapter REST" {
                        tag "PrimaryAdapter"
                    }
                    catalog_backend_application = component "Application Core" {
                        tag "Application"
                    }
                    catalog_backend_adapter_secondary_db = component "Secondary Adapter DB" {
                        tag "SecondaryAdapter"
                    }

                    catalog_backend_adapter_primary_rest -> catalog_backend_application {
                        tag "DEFINES_DEPENDENCY"
                    }

                    catalog_backend_application -> catalog_backend_adapter_secondary_db
                }
                catalog_db = database "Product Catalog DB"

                catalog_backend_adapter_secondary_db -> catalog_db "Read / Write" "JDBC"
            }

            order = group "Order Service" {
                order_backend = backend "Order Backend" {
                    tag "BoundedContext"
                    order_backend_adapter_primary_rest = component "Primary Adapter REST" {
                        tag "PrimaryAdapter"
                    }
                    order_backend_application = component "Application Core" {
                        tag "Application"
                    }
                    order_backend_adapter_secondary_db = component "Secondary Adapter DB" {
                        tag "SecondaryAdapter"
                    }

                    order_backend_adapter_primary_rest -> order_backend_application {
                        tag "DEFINES_DEPENDENCY"
                    }

                    order_backend_application -> order_backend_adapter_secondary_db
                }

                order_db = database "Order DB"

                order_backend_adapter_secondary_db -> order_db "Read / Write" "JDBC"
            }

            ui = ui "UI"
            ui -> order_backend_adapter_primary_rest "Request data and place orders" "REST" {
                properties {
                    "Communication" "Synchrounous"
                }
            }
            ui -> catalog_backend_adapter_primary_rest "Search for products" "REST" {
                properties {
                    "Communication" "Synchrounous"
                }
            }

        }

        user -> ui "Uses"

        env = deploymentEnvironment "Test Env" {
            deploymentNode "Test Node" {
                containerInstance catalog_backend
            }
        }
    }


    views {

        systemContext shop "0501-System_Context_Diagram" {
            include *
            autolayout tb
        }

        container shop "0502-Container_Diagram" {
            include *
            autolayout tb
        }

        component order_backend "0503-Component_Diagram_Order" {
            include *
            autolayout tb
        }

        component catalog_backend "0503-Component_Diagram_Catalog" {
            include *
            autolayout tb
        }
    }

}