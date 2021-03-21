package main

import (
	"fmt"

	"strconv"
	"time"
	"bytes"
	"github.com/hyperledger/fabric/core/chaincode/shim"
	pb "github.com/hyperledger/fabric/protos/peer"
)

// SimpleChaincode example simple Chaincode implementation
type SimpleChaincode struct {
}

func (t *SimpleChaincode) Init(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("trace store Init")
	// _, args := stub.GetFunctionAndParameters()

	// drug := args[0]
	// pos := args[1]

	// // Write the state back to the ledger
	// err := stub.PutState(drug, []byte(pos))
	// if err != nil {
	// 	return shim.Error(err.Error())
	// }

	return shim.Success([]byte("Trace Start!"))
}

func (t *SimpleChaincode) Invoke(stub shim.ChaincodeStubInterface) pb.Response {
	fmt.Println("Trace Invoke")
	
	function, args := stub.GetFunctionAndParameters()
	fmt.Println("function:" + function)
	
	if function == "initdrug" {
		return t.initdrug(stub, args)
	}else if function == "changedrug" {
		return t.changedrug(stub, args)
	}else if function == "history" {
		return t.history(stub, args)
	}

	return shim.Error("Invalid invoke function name. Expecting \"initdrug\" \"changedrug\" \"history\"")
}

func (t *SimpleChaincode) initdrug(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var drug string    // drug id
	var pos string // position
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	drug = args[0]
	pos = args[1]
	
	// Write the state back to the ledger
	err = stub.PutState(drug, []byte(pos))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("Init drug!"))
}

func (t *SimpleChaincode) changedrug(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var drug string
	var pos string
	var err error

	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}

	drug = args[0]
	pos = args[1]

	// Get the state from the ledger
	drugbytes, err := stub.GetState(drug)
	
	if err != nil {
		jsonResp := "{\"Error\":\"Failed to get state for " + drug + "\"}"
		return shim.Error(jsonResp)
	}

	if drugbytes == nil {
		jsonResp := "{\"Error\":\"Nil amount for " + drug + "\"}"
		return shim.Error(jsonResp)
	}

	err = stub.PutState(drug, []byte(pos))
	if err != nil {
		return shim.Error(err.Error())
	}

	return shim.Success([]byte("change position"))
}


// 这里返回json格式的数据，数据格式为
// [{"TxId":"xxx", "Value":"xxx", "Timestamp":"xxxx", "IsDelete":"xxxx"}, 
// {"TxId":"xxx", "Value":"xxx", "Timestamp":"xxxx", "IsDelete":"xxxx"} ]
func (t *SimpleChaincode) history(stub shim.ChaincodeStubInterface, args []string) pb.Response {
	var drug string
	var err error 

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting name of the person to query")
	}

	drug = args[0]
	// get history iter from the ledger 
	resultsIterator, err := stub.GetHistoryForKey(drug)
    if err != nil {
            return shim.Error(err.Error())
    }
    defer resultsIterator.Close()

    var buffer bytes.Buffer
    buffer.WriteString("[")

    bArrayMemberAlreadyWritten := false
    for resultsIterator.HasNext() {
        response, err := resultsIterator.Next()
        if err != nil {
                return shim.Error(err.Error())
        }
        // Add a comma before array members, suppress it for the first array member
        if bArrayMemberAlreadyWritten == true {
                buffer.WriteString(",")
        }
        buffer.WriteString("{\"TxId\":")
        buffer.WriteString("\"")
        buffer.WriteString(response.TxId)
        buffer.WriteString("\"")

        buffer.WriteString(", \"Value\":")
        if response.IsDelete {
                buffer.WriteString("null")
        } else {
                buffer.WriteString(string(response.Value))
        }

        buffer.WriteString(", \"Timestamp\":")
        buffer.WriteString("\"")
        buffer.WriteString(time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).String())
        buffer.WriteString("\"")

        buffer.WriteString(", \"IsDelete\":")
        buffer.WriteString("\"")
        buffer.WriteString(strconv.FormatBool(response.IsDelete))
        buffer.WriteString("\"")

        buffer.WriteString("}")
        bArrayMemberAlreadyWritten = true
    }
    buffer.WriteString("]")
	
	fmt.Printf("Query Response:%s\n", buffer)
	
	return shim.Success(buffer.Bytes())

}

func main() {
	err := shim.Start(new(SimpleChaincode))
	if err != nil {
		fmt.Printf("Error starting Simple chaincode: %s", err)
	}
}
