const BASE_URL = 'http://localhost:8888'

export function chatStream(memoryId, message, onChunk, onDone, onError) {
  const url = `${BASE_URL}/ai/chat?memoryId=${encodeURIComponent(memoryId)}&message=${encodeURIComponent(message)}`
  const es = new EventSource(url)

  es.onmessage = (event) => {
    onChunk(event.data)
  }

  es.onerror = () => {
    es.close()
    onDone && onDone()
  }

  return es
}
