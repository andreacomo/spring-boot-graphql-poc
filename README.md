# GraphQL for Spring playground

Trying graphQL on `http://localhost:8080/graphiql`:

* query:
  * single element
    ```graphql
    query QueryById {
      orderById(id: "109e1e4c-6fd4-4588-bc7e-a15f86413f8b") {
        creationDate
        orderDetails {
          quantity
          price
          book {
            name
            author {             
              firstName
              lastName
            }
          }
        }
      }
    }
    ```
  * list
    ```graphql
    query QueryAll {
      orders {
        id
        orderDetails {
          price
          quantity 
          book {
            name
            author {
              firstName
              lastName
            }
          }
        }
      }
    }
    ```
  * paged
    ```graphql
    query QueryPage2 {
      ordersScrollable(first: 2, after: "T18x") {
        edges {
          cursor
          node {
            id
            creationDate
          }
        }
        pageInfo {
          hasNextPage
          hasPreviousPage
          startCursor
          endCursor
        }
      }
    }
    ```
* mutation
  * creation
    ```graphql
    mutation MyMutation {
      createOrder(orderDetails: [{
        quantity: 1,
        bookId: "450eec82-f79f-4226-a200-70f934b740f0",
        price: 1.5
      }]) {
        id
        orderDetails {
          id
        }
      }
    }
    ```