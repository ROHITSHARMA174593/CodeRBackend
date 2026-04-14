# Technical Explanation: Real-time Streaming with SseEmitter

Aapne poocha ki streaming kaise kaam kar rahi hai, toh chaliye isse step-by-step samajhte hain.

## 1. Problem kya thi? (The Why)
Pehle jab student code submit karta tha, toh saara execution backend mein ek sath hota tha. Agar 50 test cases hain aur execution mein 10 second lag rahe hain, toh student ko 10 second tak sirf ek spinning loader dikhta tha. 
**Goal**: Humein student ko live dikhana tha ki "1/10 passed", "2/10 passed"... bilkul LeetCode ki tarah.

## 2. SseEmitter (Server-Sent Events)
Standard HTTP requests mein server sirf ek baar "Response" bhejta hai aur connection close ho jata hai. Lekin `SseEmitter` connection ko **Open** rakhta hai.

- **ProblemController.java**: Yahan humne `produces = MediaType.TEXT_EVENT_STREAM_VALUE` use kiya hai. Iska matlab server browser ko bol raha hai: *"Main tumhe ek stream bhej raha hoon, connection band mat karna."*
- **SseEmitter**: Ye server ke paas ek "Handle" ki tarah hai. Jab bhi backend mein ek test case pass hota hai, hum `emitter.send(response)` call karte hain, aur wo turant client ke paas pahunch jata hai.

## 3. The Service Loop (CodeExecutionService.java)
Service layer mein humne `executeInternallyStreaming` method banayi hai.

- **Consumer**: Humne isme ek `Consumer<SubmissionResponse>` pass kiya hai. 
- **Internal Loop**: Loop ke andar jaise hi ek test case compare hota hai aur result "Pass" hota hai, hum `emitter.accept(...)` call karte hain.
- **Process Output Reading**: Hum process ke output ko line-by-line read kar rahe hain. Jaise hi humein `---CASE_END---` milta hai, hum samajh jate hain ki ek case khatam hua, aur hum turant controller ko signal bhej dete hain.

## 4. Frontend: Reading the Stream
Frontend par streaming data ko handle karna thoda alag hota hai.

- **ReadableStream**: standard `axios` ya `api.post` isme kaam nahi aate kyunki wo poore response ka wait karte hain. Isliye humne **`fetch`** API ka use kiya.
- **Reader Loop**: `const { value, done } = await reader.read();`
  Ye loop tab tak chalta hai jab tak backend connection close nahi kar deta. Har baar jab server se "data: ..." aata hai, hum use `JSON.parse` karke `setResult` kar dete hain. 
  Isi wajah se aapko UI par progress bar "Live" badhta hua dikhta hai.

## Architecture Diagram

```mermaid
sequenceDiagram
    participant UI as Frontend (React)
    participant CTRL as Controller (SpringBoot)
    participant SVC as Service (CodeExecution)
    participant PROC as Main.java (Process)

    UI->>CTRL: POST /submit-stream
    CTRL->>SVC: Start Streaming Execution
    SVC->>PROC: Run User Code
    loop For each Test Case
        PROC-->>SVC: Execution Result (---CASE_END---)
        SVC->>CTRL: Consumer.accept(Progress: X/Y)
        CTRL-->>UI: SseEmitter.send(Data chunk)
        UI->>UI: Update Progress Bar (X/Y)
    </div>
    SVC->>CTRL: Final Result (Accepted/WA)
    CTRL-->>UI: SseEmitter.send(Final Data)
    CTRL->>CTRL: emitter.complete()
    CTRL-->>UI: Close Connection
```

## Summary of Tools Used:
1.  **SseEmitter**: Backend connection ko open rakhne ke liye.
2.  **Threads**: Backend mein execution ko alag thread mein chalate hain taki `SseEmitter` return ho sake (Asynchronous processing).
3.  **ReadableStream (Frontend)**: Response body ko "Tukdon" (Chunks) mein read karne ke liye.
4.  **NDJSON (Newline Delimited JSON)**: Har ek update ko ek naye event (`data: ...`) ki tarah bheja jata hai.

Is implementation se aapka project ek basic web app se hat kar ek **real-time platform** ban gaya hai!
